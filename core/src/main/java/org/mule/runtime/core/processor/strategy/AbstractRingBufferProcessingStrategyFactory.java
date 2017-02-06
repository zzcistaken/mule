/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static org.mule.runtime.core.processor.strategy.RingBufferConfig.WaitStrategy.LITE_BLOCKING;
import static reactor.util.concurrent.QueueSupplier.SMALL_BUFFER_SIZE;
import static reactor.util.concurrent.QueueSupplier.isPowerOfTwo;

import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.Pipeline;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.Sink;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategyFactory;
import org.mule.runtime.core.processor.strategy.sink.RingBufferSink;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Creates ring-buffer based processing strategy instances. These processing strategies de-multiplex incoming messages using a
 * ring-buffer which can then be subscribed to n times.
 *
 * This processing strategy is not suitable for transactional flows and will fail if used with an active transaction.
 *
 * @since 4.0
 */
public abstract class AbstractRingBufferProcessingStrategyFactory
    implements ProcessingStrategyFactory, RingBufferConfig {

  protected static String RING_BUFFER_SCHEDULER_NAME_SUFFIX = ".ring-buffer";


  private int bufferSize = DEFAULT_BUFFER_SIZE;
  private WaitStrategy waitStrategy = DEFAULT_WAIT_STRATEGY;
  private Integer subscriberCount;

  @Override
  public void setBufferSize(int bufferSize) {
    if (!isPowerOfTwo(bufferSize)) {
      throw new IllegalArgumentException("bufferSize must be a power of 2 : " + bufferSize);
    }
    this.bufferSize = bufferSize;
  }

  @Override
  public void setSubscriberCount(int subscriberCount) {
    this.subscriberCount = subscriberCount;
  }

  @Override
  public void setWaitStrategy(WaitStrategy waitStrategy) {
    this.waitStrategy = waitStrategy;
  }

  protected int getBufferSize() {
    return bufferSize;
  }

  protected Integer getSubscriberCount() {
    return subscriberCount;
  }

  protected WaitStrategy getWaitStrategy() {
    return waitStrategy;
  }

  static protected class RingBufferProcessingStrategy implements ProcessingStrategy {

    private Supplier<Scheduler> ringBufferSchedulerSupplier;
    private int bufferSize;
    private int subscribers;
    private WaitStrategy waitStrategy = DEFAULT_WAIT_STRATEGY;
    private MuleContext muleContext;
    private Consumer<Event> eventConsumer;

    protected RingBufferProcessingStrategy(Supplier<Scheduler> ringBufferSchedulerSupplier, int bufferSize, int subscribers,
                                           WaitStrategy waitStrategy,
                                           Consumer<Event> eventConsumer,
                                           MuleContext muleContext) {
      this.ringBufferSchedulerSupplier = ringBufferSchedulerSupplier;
      this.bufferSize = bufferSize;
      this.subscribers = subscribers;
      this.waitStrategy = waitStrategy;
      this.eventConsumer = eventConsumer;
      this.muleContext = muleContext;
    }

    @Override
    public Sink createSink(Pipeline pipeline, ReactiveProcessor processor) {
      return new RingBufferSink(processor, ringBufferSchedulerSupplier, bufferSize, subscribers, waitStrategy,
                                eventConsumer);
    }

    protected MuleContext getMuleContext() {
      return this.muleContext;
    }

  }

}
