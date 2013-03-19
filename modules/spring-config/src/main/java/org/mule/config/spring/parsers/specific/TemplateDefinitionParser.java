package org.mule.config.spring.parsers.specific;

import org.mule.config.spring.parsers.MuleDefinitionParserConfiguration;
import org.mule.config.spring.parsers.generic.OrphanDefinitionParser;
import org.mule.construct.Flow;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 *
 */
public class TemplateDefinitionParser extends OrphanDefinitionParser
{

    public TemplateDefinitionParser()
    {
        super(Flow.class, true);
        addIgnored("extends");
        addIgnored("name");
        addIgnored("processingStrategy");
        addIgnored("extends");
        addIgnored("templateProperties");
        addBeanFlag("abstract");
    }

    @Override
    public MuleDefinitionParserConfiguration addIgnored(String propertyName)
    {
        if (propertyName.equals("abstract"))
        {
            return this;
        }
        return super.addIgnored(propertyName);
    }

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context)
    {
        AbstractBeanDefinition abstractBeanDefinition = super.parseInternal(element, context);
        abstractBeanDefinition.setAbstract(true);
        String anExtends = element.getAttribute("extends");
        if (anExtends != null && !anExtends.trim().equals(""))
        {
            abstractBeanDefinition.setParentName(anExtends);
        }
        return abstractBeanDefinition;
    }
}
