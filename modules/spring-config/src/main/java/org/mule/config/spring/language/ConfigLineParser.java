package org.mule.config.spring.language;

import org.mule.api.lang.ConfigLine;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.Map;
import java.util.Optional;

public interface ConfigLineParser {

    void register(ConfigParserRegistry configParserRegistry);

    MuleBeanDefinitionHolder parse(ConfigLine configLine);

}
