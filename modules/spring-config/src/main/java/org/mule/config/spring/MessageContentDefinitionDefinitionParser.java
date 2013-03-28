package org.mule.config.spring;

import org.mule.config.spring.parsers.generic.ChildDefinitionParser;

/**
 *
 */
public class MessageContentDefinitionDefinitionParser extends ChildDefinitionParser
{

    public MessageContentDefinitionDefinitionParser(String setterMethod)
    {
        super(setterMethod, MessageContentDefinition.class);
        addIgnored("properties");
    }
}
