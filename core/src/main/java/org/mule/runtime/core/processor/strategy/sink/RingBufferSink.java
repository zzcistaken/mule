/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy.sink;

import static reactor.core.publisher.WorkQueueProcessor.share;
import static reactor.util.concurrent.QueueSupplier.isPowerOfTwo;
import static reactor.util.concurrent.WaitStrategy.blocking;
import static reactor.util.concurrent.WaitStrategy.busySpin;
import static reactor.util.concurrent.WaitStrategy.liteBlocking;
import static reactor.util.concurrent.WaitStrategy.parking;
import static reactor.util.concurrent.WaitStrategy.phasedOffLiteLock;
import static reactor.util.concurrent.WaitStrategy.sleeping;
import static reactor.util.concurrent.WaitStrategy.yielding;

import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.processor.strategy.RingBufferConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import reactor.core.Disposable;
import reactor.core.publisher.WorkQueueProcessor;
import reactor.util.concurrent.WaitStrategy;

/**
 * Sink that creates a sink per-thread.
 */
public class RingBufferSink extends AbstractReactorProcessorSink {

  /**
   * Creates a {@link RingBufferSink}.
   * 
   * @param processor the processor to process events emitted onto stream, typically this processor will represent the flow
   *        pipeline.
   * @param scheduler scheduler to use to obtain threads to subscribe to ring-buffer.
   * @param bufferSize the size of the ring-buffer to use (this value but be a power of two.
   * @param subscribers the number of subscribers to subscribe to ring-buffer with.
   * @param waitStrategy the wait strategy to use when waiting for {@link Event};s on ring-bugger.
   * @param eventConsumer event consumer called just before {@link Event}'s emission.
   */
  public RingBufferSink(ReactiveProcessor processor, Supplier<Scheduler> scheduler, int bufferSize,
                        int subscribers,
                        RingBufferConfig.WaitStrategy waitStrategy, Consumer<Event> eventConsumer) {
    super(eventConsumer);
    if (!isPowerOfTwo(bufferSize)) {
      throw new IllegalArgumentException("bufferSize must be a power of 2 : " + bufferSize);
    }
    WorkQueueProcessor<Event> workQueueProcessor =
        share(scheduler.get(), bufferSize, getReactorWaitStrategy(waitStrategy), false);
    List<Disposable> disposables = new ArrayList<>();
    for (int i = 0; i < subscribers; i++) {
      disposables.add(workQueueProcessor.transform(processor).retry().subscribe());
    }
    setReactorSink(new ReactorSink(workQueueProcessor.connectSink(),
                                   () -> disposables.forEach(disposable -> disposable.dispose())));
  }

  private WaitStrategy getReactorWaitStrategy(RingBufferConfig.WaitStrategy waitStrategy) {
    switch (waitStrategy) {
      case BLOCKING:
        return blocking();
      case BUSY_SPIN:
        return busySpin();
      case PARKING:
        return parking();
      case LITE_BLOCKING:
        return liteBlocking();
      case PHASED:
        return phasedOffLiteLock(200, 100, TimeUnit.MILLISECONDS);
      case SLEEPING:
        return sleeping();
      case YIELDING:
        return yielding();
      default:
        return blocking();
    }
  }

}
