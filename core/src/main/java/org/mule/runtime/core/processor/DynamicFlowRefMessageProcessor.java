/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor;

import static reactor.core.Exceptions.propagate;
import static reactor.core.publisher.Flux.from;
import static reactor.core.publisher.Flux.just;
import org.mule.runtime.core.AbstractAnnotatedObject;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.construct.FlowConstructAware;
import org.mule.runtime.core.api.context.MuleContextAware;
import org.mule.runtime.core.api.el.ExpressionLanguage;
import org.mule.runtime.core.api.lifecycle.Disposable;
import org.mule.runtime.core.api.lifecycle.Initialisable;
import org.mule.runtime.core.api.lifecycle.Startable;
import org.mule.runtime.core.api.processor.MessageProcessorChain;
import org.mule.runtime.core.api.processor.Processor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.reactivestreams.Publisher;

public class DynamicFlowRefMessageProcessor extends AbstractAnnotatedObject implements Processor, Disposable {

  private static final String NULL_FLOW_CONTRUCT_NAME = "null";
  private static final String MULE_PREFIX = "_mule-";

  private final FlowConstruct flowConstruct;
  private final String referenceExpression;
  private final MuleContext muleContext;
  private final ExpressionLanguage expressionLanguage;
  private final ConcurrentMap<String, Processor> referenceCache = new ConcurrentHashMap<>();

  public DynamicFlowRefMessageProcessor(String referenceExpression, ExpressionLanguage expressionLanguage,
                                        MuleContext muleContext, FlowConstruct flowConstruct) {
    this.referenceExpression = referenceExpression;
    this.expressionLanguage = expressionLanguage;
    this.muleContext = muleContext;
    this.flowConstruct = flowConstruct;
  }

  protected Processor createDynamicReferenceMessageProcessor() throws MuleException {

    if (!referenceCache.containsKey(referenceExpression)) {
      Processor dynamicReference = new Processor() {

        @Override
        public Event process(Event event) throws MuleException {
          final Processor dynamicMessageProcessor = resolveReferencedProcessor(event);
          // Because this is created dynamically annotations cannot be injected by Spring and so
          // FlowRefMessageProcessor is not used here.
          return ((Processor) event1 -> dynamicMessageProcessor.process(event1)).process(event);
        }

        @Override
        public Publisher<Event> apply(Publisher<Event> publisher) {
          return from(publisher).concatMap(event -> {
            try {
              return just(event).transform(resolveReferencedProcessor(event));
            } catch (MuleException e) {
              throw propagate(e);
            }
          });
        }

        private Processor resolveReferencedProcessor(Event event) throws MuleException {
          // Need to initialize because message processor won't be managed by parent
          String flowName = expressionLanguage.parse(referenceExpression, event, flowConstruct);
          final Processor dynamicMessageProcessor = getReferencedFlow(flowName, flowConstruct);
          return dynamicMessageProcessor;
        }
      };
      if (dynamicReference instanceof Initialisable) {
        ((Initialisable) dynamicReference).initialise();
      }
      referenceCache.putIfAbsent(referenceExpression, dynamicReference);
    }
    return referenceCache.get(referenceExpression);
  }

  protected Processor getReferencedFlow(String flowName, FlowConstruct flowConstruct) throws MuleException {
    String categorizedName = getReferencedFlowCategorizedName(flowName, flowConstruct);
    if (!referenceCache.containsKey(categorizedName)) {
      Processor referencedFlow = muleContext.getRegistry().get(flowName);
      if (referencedFlow instanceof Initialisable) {
        if (referencedFlow instanceof FlowConstructAware) {
          ((FlowConstructAware) referencedFlow).setFlowConstruct(flowConstruct);
        }
        if (referencedFlow instanceof MuleContextAware) {
          ((MuleContextAware) referencedFlow).setMuleContext(muleContext);
        }
        if (referencedFlow instanceof MessageProcessorChain) {
          for (Processor processor : ((MessageProcessorChain) referencedFlow).getMessageProcessors()) {
            if (processor instanceof FlowConstructAware) {
              ((FlowConstructAware) processor).setFlowConstruct(flowConstruct);
            }
            if (processor instanceof MuleContextAware) {
              ((MuleContextAware) processor).setMuleContext(muleContext);
            }
          }
        }
        ((Initialisable) referencedFlow).initialise();
      }
      if (referencedFlow instanceof Startable) {
        ((Startable) referencedFlow).start();
      }
      referenceCache.putIfAbsent(categorizedName, referencedFlow);
    }
    return referenceCache.get(categorizedName);
  }

  private String getReferencedFlowCategorizedName(String referencedFlowName, FlowConstruct flowConstruct) {
    String flowConstructName = flowConstruct != null ? flowConstruct.getName() : NULL_FLOW_CONTRUCT_NAME;
    return MULE_PREFIX + flowConstructName + "-" + referencedFlowName;
  }

  @Override
  public Event process(Event event) throws MuleException {
    return createDynamicReferenceMessageProcessor().process(event);
  }

  @Override
  public void dispose() {
    for (Processor processor : referenceCache.values()) {
      if (processor instanceof Disposable) {
        ((Disposable) processor).dispose();
      }
    }
  }
}
