package org.mule.config.spring.parsers.specific;

import org.mule.config.spring.parsers.generic.ChildDefinitionParser;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by pablolagreca on 11/26/15.
 */
public class ScopeMessageProcessorDefinitionParser extends ChildDefinitionParser
{

    private final ObjectBuilder objectBuilder;

    public ScopeMessageProcessorDefinitionParser(ObjectBuilder objectBuilder)
    {
        super("messageProcessor", objectBuilder.getFactoryBeanType());
        this.objectBuilder = objectBuilder;
    }

    @Override
    protected void parseChild(Element element, ParserContext parserContext, BeanDefinitionBuilder builder)
    {
        objectBuilder.collectAttributes(element, parserContext, builder);
        super.parseChild(element, parserContext, builder);
    }

}


