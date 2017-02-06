/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.Pipeline;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.Sink;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategyFactory;
import org.mule.runtime.core.processor.strategy.sink.DirectSink;

/**
 * This processing strategy processes all {@link Processor}'s in the caller thread serializing each event using a shared event
 * stream.
 */
public class SynchronousStreamProcessingStrategyFactory implements ProcessingStrategyFactory {

  public static final ProcessingStrategy SYNCHRONOUS_STREAM_PROCESSING_STRATEGY_INSTANCE = new ProcessingStrategy() {

    @Override
    public boolean isSynchronous() {
      return true;
    }

    @Override
    public Sink createSink(Pipeline pipeline, ReactiveProcessor processor) {
      return new DirectSink(processor, event -> {
      });
    }
  };

  @Override
  public ProcessingStrategy create(MuleContext muleContext, String schedulersNamePrefix) {
    return SYNCHRONOUS_STREAM_PROCESSING_STRATEGY_INSTANCE;
  }

}
