/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.el.mvel;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.construct.FlowConstruct;
import org.mule.construct.Flow;
import org.mule.construct.TemplateRedefinableAttribute;
import org.mule.el.context.AbstractMapContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mvel2.ParserContext;
import org.mvel2.integration.VariableResolver;

public class EventVariableResolverFactory extends MessageVariableResolverFactory
{

    private static final long serialVersionUID = -6819292692339684915L;

    public EventVariableResolverFactory(ParserContext parserContext, MuleContext muleContext, MuleEvent event)
    {
        super(parserContext, muleContext, event.getMessage());
        addFinalVariable("flow", new FlowContext(event.getFlowConstruct()));
        addFinalVariable("templateProperties", new TemplatePropertiesContext((Flow) event.getFlowConstruct()));
    }

    public static class FlowContext
    {
        private FlowConstruct flowConstruct;

        public FlowContext(FlowConstruct flowConstruct)
        {
            this.flowConstruct = flowConstruct;
        }

        public String getName()
        {
            return flowConstruct.getName();
        }
    }

    public static class TemplatePropertiesContext extends AbstractMapContext<String, String>
    {
        private Flow flow;

        public TemplatePropertiesContext(Flow flow)
        {
            this.flow = flow;
        }


        @Override
        public String get(Object o)
        {
            Map<String,Map<String,String>> flowsConfigurationAttributes = flow.getMuleContext().getRegistry().get("flowRedefinableAttributes");
            Map<String,String> flowRedefinableAttributes = flowsConfigurationAttributes.get(flow.getName());
            return flowRedefinableAttributes.get(o);
        }

        @Override
        public String put(String s, String s2)
        {
            throw new UnsupportedOperationException("template properties cannot be modified");
        }

        @Override
        public String remove(Object o)
        {
            throw new UnsupportedOperationException("template properties cannot be modified");
        }

        @Override
        public Set<String> keySet()
        {
            throw new UnsupportedOperationException("template properties cannot be listed");
        }
    }
}
