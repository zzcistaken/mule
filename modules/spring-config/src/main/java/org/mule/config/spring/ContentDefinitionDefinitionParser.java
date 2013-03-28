package org.mule.config.spring;

import org.mule.config.spring.parsers.generic.ChildDefinitionParser;

/**
 *
 */
public class ContentDefinitionDefinitionParser extends ChildDefinitionParser
{

    public ContentDefinitionDefinitionParser(Class<?> clazz)
    {
        super("contentDefinition", clazz);
        addIgnored("properties");
    }
}
