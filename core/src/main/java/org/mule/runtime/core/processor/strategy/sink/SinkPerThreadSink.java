/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy.sink;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.lang.Thread.currentThread;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.disposeIfNeeded;
import static org.slf4j.helpers.NOPLogger.NOP_LOGGER;

import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.construct.Pipeline;
import org.mule.runtime.core.api.processor.Sink;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;

import com.google.common.cache.Cache;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * {@link Sink} implementation that rather than de-multiplexing or serializing {@link Event}'s onto a single stream, instead uses
 * a stream per {@link Thread} thus implementing n parallel streams, onc for each source/client thread. This is more efficient
 * than using a {@link SinkPerThreadSink} and allows the uses of {@link Sink}'s that providing buffering.
 * <p/>
 * Note that when using this Sink, {@link ProcessingStrategy#onPipeline(Pipeline)} is applied for each stream and therefore if
 * processing is published to a single CPU_LITE thread as part of this in fat n threads will be used, once for each stream and
 * source thread.
 */
public class SinkPerThreadSink implements Sink, Disposable {

  private Supplier<Sink> sinkSupplier;
  private Cache<Thread, Sink> sinkCache =
      newBuilder().weakValues().removalListener(notification -> disposeIfNeeded(notification.getValue(), NOP_LOGGER)).build();

  /**
   * Create a {@link SinkPerThreadSink} that will create and use a given {@link Sink} for each distinct caller {@link Thread}.
   * 
   * @param sinkSupplier {@link Supplier} for the {@link Sink} that sould be used for each thread.
   */
  public SinkPerThreadSink(Supplier<Sink> sinkSupplier) {
    this.sinkSupplier = sinkSupplier;
  }

  @Override
  public void accept(Event event) {
    try {
      sinkCache.get(currentThread(), () -> sinkSupplier.get()).accept(event);
    } catch (ExecutionException e) {
      throw new IllegalStateException("Unable to create Sink for Thread " + currentThread(), e);
    }
  }

  @Override
  public void dispose() {
    disposeIfNeeded(sinkCache.asMap().entrySet(), NOP_LOGGER);
    sinkCache.invalidateAll();
  }

}
