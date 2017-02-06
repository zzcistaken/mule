/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static org.mule.runtime.core.processor.strategy.ProcessingStrategyUtils.NOP_EVENT_CONSUMER;

import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.Pipeline;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.Sink;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategyFactory;
import org.mule.runtime.core.processor.strategy.sink.DirectSink;
import org.mule.runtime.core.processor.strategy.sink.SinkPerThreadSink;

/**
 * Processing strategy that processes all {@link Processor}'s in the caller thread. Unlike other, asynchronous, processing
 * strategies this processing strategy does not used a shared stream, given this would require serializing all requests and
 * limiting the effectiveness of multi-threaded sources and operations. Use {@link SynchronousStreamProcessingStrategyFactory} in
 * order to obtain stream semantics while doing all processing in the caller thread.
 */
public class SynchronousProcessingStrategyFactory implements ProcessingStrategyFactory {

  public static final ProcessingStrategy SYNCHRONOUS_PROCESSING_STRATEGY_INSTANCE =
      new ProcessingStrategy() {

        @Override
        public boolean isSynchronous() {
          return true;
        }

        @Override
        public Sink createSink(Pipeline pipeline, ReactiveProcessor processor) {
          return new SinkPerThreadSink(() -> new DirectSink(processor, NOP_EVENT_CONSUMER));
        }
      };

  @Override
  public ProcessingStrategy create(MuleContext muleContext, String schedulersNamePrefix) {
    return SYNCHRONOUS_PROCESSING_STRATEGY_INSTANCE;
  }

}
