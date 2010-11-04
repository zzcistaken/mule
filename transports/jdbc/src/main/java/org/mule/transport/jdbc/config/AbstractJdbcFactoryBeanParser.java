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
public abstract class AbstractJdbcFactoryBeanParser extends AbstractBeanDefinitionParser
{

    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext)
    {
        final MutablePropertyValues parentProps = parserContext.getContainingBeanDefinition().getPropertyValues();

        final String ref = element.getAttribute(AbstractMuleBeanDefinitionParser.ATTRIBUTE_REF);
        final String clazz = element.getAttribute(AbstractMuleBeanDefinitionParser.ATTRIBUTE_CLASS);
        if (StringUtils.isBlank(ref) && StringUtils.isBlank(clazz))
        {
            throw new IllegalArgumentException("Neither ref nor class attribute specified for the " + getPropertyName() + " element");
        }


        if (StringUtils.isNotBlank(ref))
        {
            // Adds a ref to other bean
            parentProps.addPropertyValue(getPropertyName(), new RuntimeBeanReference(ref));
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

            parentProps.addPropertyValue(getPropertyName(), strategy);
        }

        return null;
    }

    /**
     * Returns the name of the property that represents the bean.
     */
    protected abstract String getPropertyName();
}

