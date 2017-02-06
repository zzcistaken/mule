/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy.sink;

import static reactor.core.publisher.EmitterProcessor.create;
import static reactor.core.publisher.Flux.create;
import static reactor.core.publisher.WorkQueueProcessor.share;
import static reactor.util.concurrent.QueueSupplier.isPowerOfTwo;
import static reactor.util.concurrent.WaitStrategy.parking;

import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.processor.ReactiveProcessor;

import java.util.function.Consumer;

import reactor.core.publisher.EmitterProcessor;

/**
 * Sink that creates a sink per-thread.
 */
public class BufferedSink extends AbstractReactorProcessorSink {

  /**
   * Creates a {@link BufferedSink} implemented using an {@link EmitterProcessor} in order to support retry.
   *
   * @param processor the processor to process events emitted onto stream, typically this processor will represent the flow
   *        pipeline.
   * @param bufferSize the size of the ring-buffer to use (this value but be a power of two).
   * @param eventConsumer event consumer called just before {@link Event}'s emission.
   */
  public BufferedSink(ReactiveProcessor processor, int bufferSize,
                      Consumer<Event> eventConsumer) {
    super(eventConsumer);
    if (!isPowerOfTwo(bufferSize)) {
      throw new IllegalArgumentException("bufferSize must be a power of 2 : " + bufferSize);
    }
    EmitterProcessor<Event> emitterProcessor = create(bufferSize, false);
    setReactorSink(new ReactorSink(emitterProcessor.serialize().connectSink(),
                                   emitterProcessor.transform(processor).retry().subscribe()));
  }

}
