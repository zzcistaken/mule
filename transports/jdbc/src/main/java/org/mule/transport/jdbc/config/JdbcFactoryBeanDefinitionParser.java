/*
 * $Id$
 * --------------------------------------------------------------------------------------
 *
 * (c) 2003-2008 MuleSource, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSource's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSource. If such an agreement is not in place, you may not use the software.
 */

package org.mule.transport.jdbc.config;

import org.mule.config.spring.parsers.AbstractMuleBeanDefinitionParser;
import org.mule.util.ClassUtils;
import org.mule.util.StringUtils;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Defines a bean definition parser that checks for ref and class attributes.
 */
public class JdbcFactoryBeanDefinitionParser extends AbstractBeanDefinitionParser
{

    private final String propertyName;

    public JdbcFactoryBeanDefinitionParser(String propertyName) {
        this.propertyName = propertyName;
    }

    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext)
    {
        final MutablePropertyValues parentProps = parserContext.getContainingBeanDefinition().getPropertyValues();

        final String ref = element.getAttribute(AbstractMuleBeanDefinitionParser.ATTRIBUTE_REF);
        final String clazz = element.getAttribute(AbstractMuleBeanDefinitionParser.ATTRIBUTE_CLASS);
        if (StringUtils.isBlank(ref) && StringUtils.isBlank(clazz))
        {
            throw new IllegalArgumentException("Neither ref nor class attribute specified for the " + propertyName + " element");
        }


        if (StringUtils.isNotBlank(ref))
        {
            // Adds a ref to other bean
            parentProps.addPropertyValue(propertyName, new RuntimeBeanReference(ref));
        }
        else
        {
            // Class attributed specified, instantiate and set directly
            final Object strategy;
            try
            {
                strategy = ClassUtils.instanciateClass(clazz, ClassUtils.NO_ARGS, getClass());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }

            parentProps.addPropertyValue(propertyName, strategy);
        }

        return null;
    }
}

