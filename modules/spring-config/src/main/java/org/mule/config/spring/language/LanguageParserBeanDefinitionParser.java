package org.mule.config.spring.language;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class LanguageParserBeanDefinitionParser extends AbstractBeanDefinitionParser {

    private final ConfigParserRegistry configParserRegistry;

    public LanguageParserBeanDefinitionParser(ConfigParserRegistry configParserRegistry) {
        this.configParserRegistry = configParserRegistry;
    }

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
//        ConfigLine configLine = configLineFromElement(element, () -> {
//            return null;
//        }).get();

//        configParserRegistry.parse(configLine, parserContext);
        return null;
    }



    //TODO makes parsers discoverables
    public LanguageParserBeanDefinitionParser addParser(ConfigLineParser configLineParser) {
        configLineParser.register(configParserRegistry);
        return this;
    }
}
