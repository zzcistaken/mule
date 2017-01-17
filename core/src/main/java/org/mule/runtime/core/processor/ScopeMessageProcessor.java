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
import static org.slf4j.LoggerFactory.getLogger;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Lifecycle;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.construct.FlowConstructAware;
import org.mule.runtime.core.api.context.MuleContextAware;
import org.mule.runtime.core.api.processor.MessageProcessorsOwner;
import org.mule.runtime.core.api.processor.Processor;

import java.util.List;

import org.slf4j.Logger;

/**
 * TODO
 */
public class ScopeMessageProcessor implements Processor, MessageProcessorsOwner, Lifecycle, MuleContextAware, FlowConstructAware {

  private final static Logger logger = getLogger(ScopeMessageProcessor.class);

  private Processor scopeMessageProcessor;
  private List<Processor> childMessageProcessors;
  private FlowConstruct flowConstruct;
  private MuleContext context;

  @Override
  public void initialise() throws InitialisationException {
    initialiseIfNeeded(scopeMessageProcessor, context, flowConstruct);
    initialiseIfNeeded(childMessageProcessors, context, flowConstruct);
  }

  @Override
  public void start() throws MuleException {
    startIfNeeded(childMessageProcessors);
    startIfNeeded(scopeMessageProcessor);
  }

  @Override
  public void stop() throws MuleException {
    stopIfNeeded(childMessageProcessors);
    stopIfNeeded(scopeMessageProcessor);
  }

  @Override
  public void dispose() {
    disposeIfNeeded(childMessageProcessors, logger);
    disposeIfNeeded(scopeMessageProcessor, logger);
  }

  @Override
  public void setFlowConstruct(FlowConstruct flowConstruct) {
    this.flowConstruct = flowConstruct;
  }

  @Override
  public List<Processor> getChildMessageProcessors() {
    return this.childMessageProcessors;
  }

  @Override
  public void setMuleContext(MuleContext context) {
    this.context = context;
  }

  @Override
  public ProcessingType getProcessingType() {
    return scopeMessageProcessor.getProcessingType();
  }

  @Override
  public Event process(Event event) throws MuleException {
    return scopeMessageProcessor.process(event);
  }

}
