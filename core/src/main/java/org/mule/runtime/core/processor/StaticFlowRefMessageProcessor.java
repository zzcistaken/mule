/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor;

import static reactor.core.publisher.Flux.from;
import org.mule.runtime.core.AbstractAnnotatedObject;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.processor.Processor;

import org.reactivestreams.Publisher;

public class StaticFlowRefMessageProcessor extends AbstractAnnotatedObject implements Processor {

  private final Processor referencedFlow;

  public StaticFlowRefMessageProcessor(Processor referencedFlow) {
    this.referencedFlow = referencedFlow;
  }

  @Override
  public Publisher<Event> apply(Publisher<Event> publisher) {
    return from(publisher).transform(referencedFlow);
  }

  @Override
  public Event process(Event event) throws MuleException {
    return referencedFlow.process(event);
  }

}
