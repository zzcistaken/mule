/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring.dsl.processor;

import static org.mule.runtime.core.config.i18n.CoreMessages.objectIsNull;
import org.mule.runtime.config.spring.dsl.api.ObjectFactory;
import org.mule.runtime.core.AbstractAnnotatedObject;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleRuntimeException;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.construct.FlowConstructAware;
import org.mule.runtime.core.api.el.ExpressionLanguage;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.construct.Flow;
import org.mule.runtime.core.processor.DynamicFlowRefMessageProcessor;
import org.mule.runtime.core.processor.StaticFlowRefMessageProcessor;
import org.mule.runtime.core.processor.StaticSubFlowRefMessageProcessor;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;

public class FlowRefObjectFactory extends AbstractAnnotatedObject implements ObjectFactory<Processor>, FlowConstructAware {

  @Inject
  private ExpressionLanguage expressionLanguage;
  @Inject
  private ApplicationContext applicationContext;
  @Inject
  private MuleContext muleContext;

  private FlowConstruct flowConstruct;
  private String name;

  @Override
  public Processor getObject() throws Exception {
    if (name.isEmpty()) {
      throw new MuleRuntimeException(objectIsNull("flow reference is empty"));
    }
    Processor flowRefProcessor;
    if (expressionLanguage.isExpression(name)) {
      flowRefProcessor = new DynamicFlowRefMessageProcessor(name, expressionLanguage, muleContext, flowConstruct);
    } else {
      final Processor referencedFlow = ((Processor) applicationContext.getBean(name));
      if (referencedFlow instanceof Flow) {
        flowRefProcessor = new StaticFlowRefMessageProcessor(referencedFlow);
      } else {
        flowRefProcessor = new StaticSubFlowRefMessageProcessor(referencedFlow, muleContext, flowConstruct);
      }
    }
    return flowRefProcessor;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setFlowConstruct(FlowConstruct flowConstruct) {
    this.flowConstruct = flowConstruct;
  }
}
