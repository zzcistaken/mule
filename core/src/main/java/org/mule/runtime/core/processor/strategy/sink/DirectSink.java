/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy.sink;

import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.Sink;

import java.util.function.Consumer;

import reactor.core.publisher.BlockingSink;
import reactor.core.publisher.DirectProcessor;

/**
 * {@link Sink} implementation that emits {@link Event}'s via a single stream directly without any de-multiplexing or buffering.
 * If this {@link Sink} is called from multiplex source or client threads then {@link Event}'s will be serialized.
 */
public class DirectSink extends AbstractReactorProcessorSink {

  /**
   * Create new {@link DirectSink}.
   * 
   * @param processor the processor to process events emitted onto stream, typically this processor will represent the flow
   *        pipeline.
   * @param eventConsumer event consumer called just before {@link Event}'s emission.
   */
  public DirectSink(ReactiveProcessor processor, Consumer<Event> eventConsumer) {
    super(eventConsumer);
    DirectProcessor<Event> directProcessor = DirectProcessor.create();
    BlockingSink<Event> blockingSink = directProcessor.serialize().connectSink();
    setReactorSink(new ReactorSink(blockingSink, directProcessor.transform(processor).retry().subscribe()));
  }

}
