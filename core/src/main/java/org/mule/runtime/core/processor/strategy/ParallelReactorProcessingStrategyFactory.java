/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static org.mule.runtime.core.processor.strategy.ProcessingStrategyUtils.FAIL_IF_TX_ACTIVE_EVENT_CONSUMER;

import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.Pipeline;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.Sink;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;
import org.mule.runtime.core.api.scheduler.SchedulerConfig;
import org.mule.runtime.core.processor.strategy.sink.SinkPerThreadSink;

/**
 * Creates a processing strategy that uses a reactor strategy for each source thread.
 * <p/>
 * This processing strategy is not suitable for transactional flows and will fail if used with an active transaction.
 *
 * @since 4.0
 */
public class ParallelReactorProcessingStrategyFactory extends AbstractRingBufferProcessingStrategyFactory {

  @Override
  public ProcessingStrategy create(MuleContext muleContext, String schedulersNamePrefix) {

    int subscribers = getSubscriberCount() != null ? getSubscriberCount() : 1;

    return new RingBufferProcessingStrategy(() -> muleContext.getSchedulerService()
        .customScheduler(SchedulerConfig.config().withName(schedulersNamePrefix + RING_BUFFER_SCHEDULER_NAME_SUFFIX)
            .withMaxConcurrentTasks(subscribers + 1)),
                                            getBufferSize(),
                                            subscribers,
                                            getWaitStrategy(),
                                            FAIL_IF_TX_ACTIVE_EVENT_CONSUMER,
                                            muleContext) {

      @Override
      public Sink createSink(Pipeline pipeline, ReactiveProcessor processor) {
        return new SinkPerThreadSink(() -> super.createSink(pipeline, processor));
      }
    };
  }


}
