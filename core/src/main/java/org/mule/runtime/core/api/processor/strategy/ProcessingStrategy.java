/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.api.processor.strategy;

import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.construct.Pipeline;
import org.mule.runtime.core.api.exception.MessagingExceptionHandler;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.Sink;

import java.util.function.Function;

/**
 * Determines how a list of message processors should processed.
 */
public interface ProcessingStrategy {

  /**
   * Creates instances of {@link Sink} to be used for emitting {@link Event}'s to be processed. Each {@link Sink} should be used
   * independent streams that implement the {@link Pipeline}.
   *
   * @param pipeline pipeline instance.
   * @param processor processor representing the pipeline.
   * @return new sink instance
   */
  Sink createSink(Pipeline pipeline, ReactiveProcessor processor);

  /**
   * Enrich {@link Processor} function by adding pre/post operators to implement processing strategy behaviour.
   *
   * @param pipeline pipeline instance.
   * @return enriched pipeline function/
   */
  default Function<ReactiveProcessor, ReactiveProcessor> onPipeline(Pipeline pipeline) {
    return onPipeline(pipeline, pipeline.getExceptionListener());
  }

  /**
   * Enrich {@link Processor} function by adding pre/post operators to implement processing strategy behaviour.
   *
   * @param pipeline pipeline instance.
   * @param messagingExceptionHandler exception handle to use.
   * @return enriched pipeline function
   */
  default Function<ReactiveProcessor, ReactiveProcessor> onPipeline(Pipeline pipeline,
                                                                    MessagingExceptionHandler messagingExceptionHandler) {
    return processor -> processor;
  }

  /**
   * Enrich {@link Processor} function by adding pre/post operators to implement processing strategy behaviour.
   *
   * @return enriched processor function
   */
  default Function<ReactiveProcessor, ReactiveProcessor> onProcessor() {
    return processor -> processor;
  }

  /**
   * Whether the processing that has this instance is synchronous or not
   */
  default boolean isSynchronous() {
    return false;
  }

}
