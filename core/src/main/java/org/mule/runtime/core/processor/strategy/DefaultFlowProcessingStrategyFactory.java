/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.disposeIfNeeded;
import static org.mule.runtime.core.api.processor.ReactiveProcessor.ProcessingType.BLOCKING;
import static org.mule.runtime.core.api.processor.ReactiveProcessor.ProcessingType.CPU_INTENSIVE;
import static org.mule.runtime.core.api.processor.ReactiveProcessor.ProcessingType.CPU_LITE;
import static org.mule.runtime.core.api.scheduler.SchedulerConfig.config;
import static org.mule.runtime.core.processor.strategy.SynchronousProcessingStrategyFactory.SYNCHRONOUS_PROCESSING_STRATEGY_INSTANCE;
import static org.mule.runtime.core.transaction.TransactionCoordination.isTransactionActive;
import static org.slf4j.helpers.NOPLogger.NOP_LOGGER;

import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.Pipeline;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.Sink;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Creates default processing strategy with same behaviour as {@link ProactorProcessingStrategyFactory} apart from the fact it
 * will process synchronously without error when a transaction is active.
 */
public class DefaultFlowProcessingStrategyFactory extends ProactorProcessingStrategyFactory {

  @Override
  public ProcessingStrategy create(MuleContext muleContext, String schedulersNamePrefix) {
    int subscribers = min(getSubscriberCount(), getMaxConcurrency());
    return new DefaultFlowProcessingStrategy(() -> muleContext.getSchedulerService()
        .cpuLightScheduler(config().withName(schedulersNamePrefix + "." + CPU_LITE.name())),
                                             () -> muleContext.getSchedulerService()
                                                 .ioScheduler(config().withName(schedulersNamePrefix + "." + BLOCKING.name())),
                                             () -> muleContext.getSchedulerService()
                                                 .cpuIntensiveScheduler(config()
                                                     .withName(schedulersNamePrefix + "." + CPU_INTENSIVE.name())),
                                             scheduler -> scheduler.stop(muleContext.getConfiguration().getShutdownTimeout(),
                                                                         MILLISECONDS),
                                             getMaxConcurrency(),
                                             () -> muleContext.getSchedulerService()
                                                 .customScheduler(config()
                                                     .withName(schedulersNamePrefix + RING_BUFFER_SCHEDULER_NAME_SUFFIX)
                                                     .withMaxConcurrentTasks(subscribers + 1)),
                                             getBufferSize(),
                                             subscribers,
                                             getWaitStrategy(),
                                             muleContext);
  }

  static class DefaultFlowProcessingStrategy extends ProactorProcessingStrategy {

    protected DefaultFlowProcessingStrategy(Supplier<Scheduler> cpuLightSchedulerSupplier,
                                            Supplier<Scheduler> blockingSchedulerSupplier,
                                            Supplier<Scheduler> cpuIntensiveSchedulerSupplier,
                                            Consumer<Scheduler> schedulerStopper,
                                            int maxConcurrency,
                                            Supplier<Scheduler> ringBufferSchedulerSupplier,
                                            int bufferSize,
                                            int subscriberCount,
                                            WaitStrategy waitStrategy,
                                            MuleContext muleContext) {
      super(cpuLightSchedulerSupplier, blockingSchedulerSupplier, cpuIntensiveSchedulerSupplier, schedulerStopper,
            maxConcurrency, ringBufferSchedulerSupplier, bufferSize, subscriberCount, waitStrategy, muleContext);
    }

    @Override
    public Sink createSink(Pipeline pipeline, ReactiveProcessor processor) {
      Sink proactorSink = super.createSink(pipeline, processor);
      Sink syncSink = SYNCHRONOUS_PROCESSING_STRATEGY_INSTANCE.createSink(pipeline, processor);
      return new DelegateSink(syncSink, proactorSink);
    }

    private final static class DelegateSink implements Sink, Disposable {

      private final Sink syncSink;
      private final Sink proactorSink;

      public DelegateSink(Sink syncSink, Sink proactorSink) {
        this.syncSink = syncSink;
        this.proactorSink = proactorSink;
      }

      @Override
      public void accept(Event event) {
        if (isTransactionActive()) {
          syncSink.accept(event);
        } else {
          proactorSink.accept(event);
        }
      }

      @Override
      public void dispose() {
        disposeIfNeeded(syncSink, NOP_LOGGER);
        disposeIfNeeded(proactorSink, NOP_LOGGER);
      }
    }
  }

}
