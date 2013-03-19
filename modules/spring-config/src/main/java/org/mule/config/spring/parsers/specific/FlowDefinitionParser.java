/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.spring.parsers.specific;

import org.mule.api.config.MuleProperties;
import org.mule.config.spring.parsers.PostProcessor;
import org.mule.config.spring.parsers.assembly.BeanAssembler;
import org.mule.config.spring.parsers.generic.OrphanDefinitionParser;
import org.mule.config.spring.util.ProcessingStrategyUtils;
import org.mule.construct.Flow;

import java.util.List;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class FlowDefinitionParser extends OrphanDefinitionParser
{
    public FlowDefinitionParser()
    {
        super(Flow.class, true);
        addIgnored("extends");
        addIgnored("abstract");
        addIgnored("name");
        addIgnored("processingStrategy");
    }

    @java.lang.Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder)
    {
        builder.addConstructorArgValue(element.getAttribute(ATTRIBUTE_NAME));
        builder.addConstructorArgReference(MuleProperties.OBJECT_MULE_CONTEXT);
        ProcessingStrategyUtils.configureProcessingStrategy(element, builder,
            ProcessingStrategyUtils.QUEUED_ASYNC_PROCESSING_STRATEGY);
        super.doParse(element, parserContext, builder);
    }

    @Override
    protected String getParentName(Element element)
    {
        return element.getAttribute("extends");
    }

    @Override
    protected BeanDefinitionBuilder createBeanDefinitionBuilder(Element element, Class<?> beanClass)
    {
        String parentTemplate = element.getAttribute("extends");
        if (parentTemplate != null)
        {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.childBeanDefinition(parentTemplate);
            beanDefinitionBuilder.getBeanDefinition().setBeanClass(Flow.class);
            return beanDefinitionBuilder;
        }
        return super.createBeanDefinitionBuilder(element, beanClass);
    }

}
