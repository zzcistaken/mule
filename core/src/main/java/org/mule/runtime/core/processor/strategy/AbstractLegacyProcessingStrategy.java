/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static org.mule.runtime.core.processor.strategy.ProcessingStrategyUtils.NOP_EVENT_CONSUMER;

import org.mule.runtime.core.api.construct.Pipeline;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.Sink;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;
import org.mule.runtime.core.processor.strategy.sink.StreamPerEventSink;

/**
 * Interface to be implemented by legacy processing strategy implementations. This interface provides a default implementation of
 * {@link ProcessingStrategy#createSink(Pipeline, ReactiveProcessor)} that ensures processed events are not de-multiplexed into a
 * single {@link org.mule.runtime.core.api.construct.Flow} stream but are rather executed independently.
 */
@Deprecated
public abstract class AbstractLegacyProcessingStrategy implements ProcessingStrategy {

  @Override
  public final Sink createSink(Pipeline pipeline, ReactiveProcessor processor) {
    return new StreamPerEventSink(processor, NOP_EVENT_CONSUMER);
  }

}
