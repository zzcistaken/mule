/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.jersey.config;

import org.mule.config.spring.parsers.specific.ComponentDefinitionParser;
import org.mule.module.jersey.JerseyResourcesComponent;
import org.mule.util.StringUtils;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

final class JerseyResourceDefinitionParser extends ComponentDefinitionParser
{

    private static final String JAXB_CONTEXT_ATTRIBUTE_NAME = "jaxbContext-ref";

    JerseyResourceDefinitionParser()
    {
        super(JerseyResourcesComponent.class);
        addIgnored(JAXB_CONTEXT_ATTRIBUTE_NAME);
    }

    @Override
    protected void parseChild(Element element, ParserContext parserContext, BeanDefinitionBuilder builder)
    {
        String jaxbContextRef = element.getAttribute(JAXB_CONTEXT_ATTRIBUTE_NAME);
        if (!StringUtils.isBlank(jaxbContextRef))
        {
            builder.addPropertyValue("jaxbContextName", jaxbContextRef);
        }
        super.parseChild(element, parserContext, builder);
    }
}
