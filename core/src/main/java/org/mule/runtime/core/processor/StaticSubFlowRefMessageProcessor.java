/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor;

import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.disposeIfNeeded;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.initialiseIfNeeded;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.startIfNeeded;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.stopIfNeeded;
import static reactor.core.publisher.Flux.from;
import org.mule.runtime.core.AbstractAnnotatedObject;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.lifecycle.InitialisationException;
import org.mule.runtime.core.api.lifecycle.Lifecycle;
import org.mule.runtime.core.api.processor.Processor;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticSubFlowRefMessageProcessor extends AbstractAnnotatedObject implements Processor, Lifecycle {

  private static final Logger logger = LoggerFactory.getLogger(StaticSubFlowRefMessageProcessor.class);

  private final FlowConstruct flowConstruct;
  private final Processor referencedFlow;
  private final MuleContext muleContext;

  public StaticSubFlowRefMessageProcessor(Processor referencedFlow, MuleContext muleContext, FlowConstruct flowConstruct) {
    this.referencedFlow = referencedFlow;
    this.muleContext = muleContext;
    this.flowConstruct = flowConstruct;

  }

  @Override
  public Publisher<Event> apply(Publisher<Event> publisher) {
    return from(publisher).transform(referencedFlow);
  }

  @Override
  public Event process(Event event) throws MuleException {
    return referencedFlow.process(event);
  }

  @Override
  public void dispose() {
    disposeIfNeeded(referencedFlow, logger);
  }

  @Override
  public void initialise() throws InitialisationException {
    initialiseIfNeeded(referencedFlow, muleContext, flowConstruct);
  }

  @Override
  public void start() throws MuleException {
    startIfNeeded(referencedFlow);
  }

  @Override
  public void stop() throws MuleException {
    stopIfNeeded(referencedFlow);
  }
}
