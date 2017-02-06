/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mule.runtime.core.api.processor.ReactiveProcessor.ProcessingType.BLOCKING;
import static org.mule.runtime.core.api.processor.ReactiveProcessor.ProcessingType.CPU_INTENSIVE;
import static org.mule.runtime.core.api.scheduler.SchedulerConfig.config;
import static org.mule.runtime.core.processor.strategy.ProcessingStrategyUtils.FAIL_IF_TX_ACTIVE_EVENT_CONSUMER;
import static org.mule.runtime.core.processor.strategy.ProcessingStrategyUtils.scheduleBlocking;
import static org.mule.runtime.core.processor.strategy.ProcessingStrategyUtils.scheduleParallel;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Creates {@link RingBufferProcessingStrategyFactory} instances. This processing strategy dispatches incoming messages to
 * single-threaded event-loop.
 *
 *
 * Processing of the flow is carried out on the event-loop but which is served by a pool of worker threads from the applications
 * IO {@link Scheduler}. Processing of the flow is carried out synchronously on the worker thread until completion.
 *
 * This processing strategy is not suitable for transactional flows and will fail if used with an active transaction.
 *
 * @since 4.0
 */
public class RingBufferProcessingStrategyFactory extends AbstractRingBufferProcessingStrategyFactory implements SchedulingConfig {

  private static int DEFAULT_MAX_CONCURRENCY = Integer.MAX_VALUE;
  private int maxConcurrency = DEFAULT_MAX_CONCURRENCY;

  @Override
  public void setMaxConcurrency(int maxConcurrency) {
    if (maxConcurrency > 1) {
      throw new IllegalArgumentException("maxConcurrency must be at least 1");
    }
    this.maxConcurrency = maxConcurrency;
  }

  protected int getMaxConcurrency() {
    return maxConcurrency;
  }

  @Override
  public ProcessingStrategy create(MuleContext muleContext, String schedulersNamePrefix) {
    if (maxConcurrency == 1) {
      return new ReactorProcessingStrategyFactory().create(muleContext, schedulersNamePrefix);
    } else {
      int subscribers = getSubscriberCount() != null ? getSubscriberCount()
          : min(Runtime.getRuntime().availableProcessors(), getMaxConcurrency());
      return new SimpleRingBufferProcessingStrategy(
                                                    () -> muleContext.getSchedulerService()
                                                        .cpuIntensiveScheduler(config()
                                                            .withName(schedulersNamePrefix + "." + CPU_INTENSIVE.name())),
                                                    () -> muleContext.getSchedulerService()
                                                        .ioScheduler(config()
                                                            .withName(schedulersNamePrefix + "." + BLOCKING.name())),
                                                    scheduler -> scheduler
                                                        .stop(muleContext.getConfiguration().getShutdownTimeout(),
                                                              MILLISECONDS),
                                                    maxConcurrency,
                                                    () -> muleContext.getSchedulerService()
                                                        .customScheduler(config()
                                                            .withName(schedulersNamePrefix + RING_BUFFER_SCHEDULER_NAME_SUFFIX)
                                                            .withMaxConcurrentTasks(subscribers + 1)),
                                                    getBufferSize(),
                                                    subscribers,
                                                    getWaitStrategy(),
                                                    muleContext);
    }
  }

  static class SimpleRingBufferProcessingStrategy extends RingBufferProcessingStrategy implements Startable, Stoppable {

    private Supplier<Scheduler> blockingSchedulerSupplier;
    private Supplier<Scheduler> cpuIntensiveSchedulerSupplier;
    private Consumer<Scheduler> schedulerStopper;
    private Scheduler blockingScheduler;
    private Scheduler cpuIntensiveScheduler;
    private int maxConcurrency;

    public SimpleRingBufferProcessingStrategy(Supplier<Scheduler> blockingSchedulerSupplier,
                                              Supplier<Scheduler> cpuIntensiveSchedulerSupplier,
                                              Consumer<Scheduler> schedulerStopper,
                                              int maxConcurrency,
                                              Supplier<Scheduler> ringBufferSchedulerSupplier,
                                              int bufferSize,
                                              int subscriberCount,
                                              WaitStrategy waitStrategy,
                                              MuleContext muleContext) {
      super(ringBufferSchedulerSupplier, bufferSize, subscriberCount, waitStrategy, FAIL_IF_TX_ACTIVE_EVENT_CONSUMER,
            muleContext);
      this.blockingSchedulerSupplier = blockingSchedulerSupplier;
      this.cpuIntensiveSchedulerSupplier = cpuIntensiveSchedulerSupplier;
      this.schedulerStopper = schedulerStopper;
      this.maxConcurrency = maxConcurrency;
    }

    @Override
    public void start() throws MuleException {
      this.blockingScheduler = blockingSchedulerSupplier.get();
      this.cpuIntensiveScheduler = cpuIntensiveSchedulerSupplier.get();
    }

    @Override
    public void stop() throws MuleException {
      if (blockingScheduler != null) {
        schedulerStopper.accept(blockingScheduler);
      }
      if (cpuIntensiveScheduler != null) {
        schedulerStopper.accept(cpuIntensiveScheduler);
      }
    }

    @Override
    public Function<ReactiveProcessor, ReactiveProcessor> onProcessor() {
      return processor -> {
        if (processor.getProcessingType() == BLOCKING) {
          return scheduleBlocking(blockingScheduler, maxConcurrency).apply(processor);
        } else if (processor.getProcessingType() == CPU_INTENSIVE) {
          return scheduleParallel(cpuIntensiveScheduler, maxConcurrency).apply(processor);
        } else {
          return processor;
        }
      };
    }

  }

}
