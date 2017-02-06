/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mule.runtime.core.api.processor.ReactiveProcessor.ProcessingType.CPU_LITE;
import static org.mule.runtime.core.api.scheduler.SchedulerConfig.config;
import static org.mule.runtime.core.processor.strategy.ProcessingStrategyUtils.FAIL_IF_TX_ACTIVE_EVENT_CONSUMER;
import static org.mule.runtime.core.processor.strategy.ProcessingStrategyUtils.scheduleParallel;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.Pipeline;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Creates {@link RingBufferProcessingStrategy} instance that implements the reactor pattern by de-multiplexes incoming messages
 * onto a ring-buffer and then using one or more event-loops to handle events. By default this processing strategy uses a single
 * event-loop but this can be configured using {@link #setSubscriberCount(int)} to define a larger number of ring-buffer
 * subscribers each acting as an event loop in the a JAva NIO selector does. The processing of all configured {@link Processor}'s
 * is carried out on the event-loop and as such this processing strategy is not suitable when you have Blocking or IO bound
 * {@link Processor}'s.
 * <p/>
 * This processing strategy is not suitable for transactional flows and will fail if used with an active transaction.
 *
 * @since 4.0
 */
public class ReactorCPULiteProcessingStrategyFactory extends AbstractRingBufferProcessingStrategyFactory {

  @Override
  public ProcessingStrategy create(MuleContext muleContext, String schedulersNamePrefix) {
    return new ReactorCPULiteProcessingStrategy(() -> muleContext.getSchedulerService()
        .cpuLightScheduler(config().withName(schedulersNamePrefix + "." + CPU_LITE.name())),
                                                scheduler -> scheduler.stop(muleContext.getConfiguration().getShutdownTimeout(),
                                                                            MILLISECONDS),
                                                () -> muleContext.getSchedulerService()
                                                    .customScheduler(config()
                                                        .withName(schedulersNamePrefix + RING_BUFFER_SCHEDULER_NAME_SUFFIX)
                                                        .withMaxConcurrentTasks(getSubscriberCount() + 1)),
                                                getBufferSize(),
                                                getSubscriberCount(),
                                                getWaitStrategy(),
                                                muleContext);
  }



  static class ReactorCPULiteProcessingStrategy extends AbstractRingBufferProcessingStrategyFactory.RingBufferProcessingStrategy
      implements Startable, Stoppable {

    private Supplier<Scheduler> cpuLightSchedulerSupplier;
    private Consumer<Scheduler> schedulerStopper;
    private Scheduler cpuLightScheduler;
    private int maxConcurrency;

    public ReactorCPULiteProcessingStrategy(Supplier<Scheduler> cpuLightSchedulerSupplier,
                                            Consumer<Scheduler> schedulerStopper,
                                            Supplier<Scheduler> ringBufferSchedulerSupplier,
                                            int bufferSize,
                                            int subscriberCount,
                                            RingBufferConfig.WaitStrategy waitStrategy,
                                            MuleContext muleContext) {
      super(ringBufferSchedulerSupplier, bufferSize, subscriberCount, waitStrategy, FAIL_IF_TX_ACTIVE_EVENT_CONSUMER,
            muleContext);
      this.cpuLightSchedulerSupplier = cpuLightSchedulerSupplier;
      this.schedulerStopper = schedulerStopper;
      this.maxConcurrency = maxConcurrency;
    }

    @Override
    public void start() throws MuleException {
      this.cpuLightScheduler = cpuLightSchedulerSupplier.get();
    }

    @Override
    public void stop() throws MuleException {
      if (cpuLightScheduler != null) {
        schedulerStopper.accept(cpuLightScheduler);
      }
    }

    @Override
    public Function<ReactiveProcessor, ReactiveProcessor> onPipeline(Pipeline flowConstruct) {
      return scheduleParallel(cpuLightScheduler, maxConcurrency);
    }

  }
}

