/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static org.mule.runtime.core.processor.strategy.ProcessingStrategyUtils.FAIL_IF_TX_ACTIVE_EVENT_CONSUMER;

import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;
import org.mule.runtime.core.api.scheduler.SchedulerConfig;

/**
 * Creates {@link RingBufferProcessingStrategy} instance that implements the reactor pattern by de-multiplexes incoming messages
 * onto a ring-buffer and then using one or more event-loops to handle events. By default this processing strategy uses a single
 * event-loop but this can be configured using {@link #setSubscriberCount(int)} to define a larger number of ring-buffer
 * subscribers each acting as an event loop in the a JAva NIO selector does. The processing of all configured {@link Processor}'s
 * is carried out on the event-loop and as such this processing strategy is not suitable when you have Blocking or IO bound
 * {@link Processor}'s.
 * <p/>
 * This processing strategy is not suitable for transactional flows and will fail if used with an active transaction.
 *
 * @since 4.0
 */
public class ReactorProcessingStrategyFactory extends AbstractRingBufferProcessingStrategyFactory {

  @Override
  public ProcessingStrategy create(MuleContext muleContext, String schedulersNamePrefix) {

    return new RingBufferProcessingStrategy(() -> muleContext.getSchedulerService()
        .customScheduler(SchedulerConfig.config().withName(schedulersNamePrefix + RING_BUFFER_SCHEDULER_NAME_SUFFIX)
            .withMaxConcurrentTasks(getSubscriberCount() + 1)),
                                            getBufferSize(),
                                            getSubscriberCount(),
                                            getWaitStrategy(),
                                            FAIL_IF_TX_ACTIVE_EVENT_CONSUMER,
                                            muleContext);
  }


}
