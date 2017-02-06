/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy.sink;

import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.processor.Sink;

import java.util.function.Consumer;

import org.reactivestreams.Subscriber;
import reactor.core.publisher.BlockingSink;
import reactor.core.publisher.FluxProcessor;

/**
 * Abstract {@link Sink} implementation that uses Reactor's {@link BlockingSink} to emit {@link Event}'s onto a stream.
 * Implementations must either de-multiplex or serialize events given streams do not support concurrent
 * {@link Subscriber#onNext(Object)}'s.
 */
abstract class AbstractReactorProcessorSink implements Sink, Disposable {

  private ReactorSink reactorSink;
  private Consumer<Event> eventConsumer;

  /**
   * Create Reactor {@link FluxProcessor} based {@link Sink}. Implementations must configure a {@link ReactorSink} during
   * construction via {@link #setReactorSink(ReactorSink)}.
   *
   * @param eventConsumer event consumer called before emitting event via {@link Sink}
   */
  AbstractReactorProcessorSink(Consumer<Event> eventConsumer) {
    this.eventConsumer = eventConsumer;
  }

  @Override
  public void accept(Event event) {
    eventConsumer.accept(event);
    reactorSink.accept(event);
  }

  @Override
  public void dispose() {
    reactorSink.dispose();
  }

  protected void setReactorSink(ReactorSink reactorSink) {
    this.reactorSink = reactorSink;
  }

  /**
   * {@link Sink} that emits events using Reactor's {@link BlockingSink} onto a single stream. Disposal of this Sink results in
   * the first the completion of the {@link FluxProcessor} and then cancellation of all {@link Subscriber}'s.
   */
  protected static class ReactorSink implements Sink, Disposable {

    private final BlockingSink blockingSink;
    private final reactor.core.Disposable disposable;

    protected ReactorSink(BlockingSink blockingSink, reactor.core.Disposable disposable) {
      this.blockingSink = blockingSink;
      this.disposable = disposable;
    }

    @Override
    public void accept(Event event) {
      // TODO MULE-11449 Implement handling of back-pressure via OVERLOAD exception type.
      blockingSink.accept(event);
    }

    @Override
    public void dispose() {
      blockingSink.complete();
      disposable.dispose();
    }

  }
}
