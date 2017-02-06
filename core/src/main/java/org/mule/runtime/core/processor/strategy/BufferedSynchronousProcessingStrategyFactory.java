/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mule.runtime.core.api.scheduler.SchedulerConfig.config;
import static org.mule.runtime.core.processor.strategy.ProcessingStrategyUtils.FAIL_IF_TX_ACTIVE_EVENT_CONSUMER;
import static reactor.core.publisher.Flux.from;
import static reactor.core.scheduler.Schedulers.fromExecutorService;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.Pipeline;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.Sink;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategyFactory;
import org.mule.runtime.core.processor.strategy.sink.BufferedSink;
import org.mule.runtime.core.processor.strategy.sink.SinkPerThreadSink;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Creates {@link BufferedSynchronousProcessingStrategy}. This processing strategy is a variant of the
 * {@link SynchronousProcessingStrategyFactory}
 *
 * @since 4.0
 */
public class BufferedSynchronousProcessingStrategyFactory implements ProcessingStrategyFactory, BufferConfig {


  private int bufferSize = DEFAULT_BUFFER_SIZE;

  @Override
  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  @Override
  public ProcessingStrategy create(MuleContext muleContext, String schedulersNamePrefix) {
    return new BufferedSynchronousProcessingStrategy(() -> muleContext.getSchedulerService()
        .customScheduler(config().withName(schedulersNamePrefix + "." + "async").withMaxConcurrentTasks(100)),
                                                     scheduler -> scheduler
                                                         .stop(muleContext.getConfiguration().getShutdownTimeout(),
                                                               MILLISECONDS),
                                                     bufferSize);
  }


  static class BufferedSynchronousProcessingStrategy
      implements ProcessingStrategy, Startable, Stoppable {

    private Supplier<Scheduler> cpuLightSchedulerSupplier;
    private Consumer<Scheduler> schedulerStopper;
    private Scheduler cpuLightScheduler;
    private int bufferSize;

    public BufferedSynchronousProcessingStrategy(Supplier<Scheduler> cpuLightSchedulerSupplier,
                                                 Consumer<Scheduler> schedulerStopper,
                                                 int bufferSize) {
      this.cpuLightSchedulerSupplier = cpuLightSchedulerSupplier;
      this.schedulerStopper = schedulerStopper;
      this.bufferSize = bufferSize;
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
    public Sink createSink(Pipeline pipeline, ReactiveProcessor processor) {
      return new SinkPerThreadSink(() -> new BufferedSink(processor, bufferSize, FAIL_IF_TX_ACTIVE_EVENT_CONSUMER));
    }

    @Override
    public Function<ReactiveProcessor, ReactiveProcessor> onPipeline(Pipeline flowConstruct) {
      return processor -> publisher -> from(publisher).transform(processor).subscribeOn(fromExecutorService(cpuLightScheduler));
    }

  }
}

