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
import org.mule.config.spring.parsers.MuleDefinitionParserConfiguration;
import org.mule.config.spring.parsers.PostProcessor;
import org.mule.config.spring.parsers.assembly.BeanAssembler;
import org.mule.config.spring.parsers.generic.OrphanDefinitionParser;
import org.mule.config.spring.util.ProcessingStrategyUtils;
import org.mule.construct.Flow;

import java.util.List;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
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
        addBeanFlag("abstract");
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
        String anExtends = element.getAttribute("extends");
        if (anExtends != null && !anExtends.trim().equals(""))
        {
            return anExtends;
        }
        return super.getParentName(element);
    }

    @Override
    protected BeanDefinitionBuilder createBeanDefinitionBuilder(Element element, Class<?> beanClass)
    {
        String parentTemplate = element.getAttribute("extends");
        if (parentTemplate != null && !parentTemplate.trim().equals(""))
        {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.childBeanDefinition(parentTemplate);
            beanDefinitionBuilder.getBeanDefinition().setBeanClass(Flow.class);
            return beanDefinitionBuilder;
        }
        return super.createBeanDefinitionBuilder(element, beanClass);
    }

    @Override
    public MuleDefinitionParserConfiguration addIgnored(String propertyName)
    {
        //if (propertyName.equals("abstract"))
        //{
        //    return this;
        //}
        return super.addIgnored(propertyName);
    }

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context)
    {
        if (element.hasAttribute("abstract"))
        {
            addBeanFlag("abstract");
        }
        AbstractBeanDefinition abstractBeanDefinition = super.parseInternal(element, context);
        if (element.hasAttribute("abstract"))
        {
            abstractBeanDefinition.setAbstract(true);
        }
        String anExtends = element.getAttribute("extends");
        if (anExtends != null && !anExtends.trim().equals(""))
        {
            abstractBeanDefinition.setParentName(anExtends);
        }
        return abstractBeanDefinition;
    }


}
