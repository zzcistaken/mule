package org.mule.config.spring.language;

import org.mule.api.lang.ConfigLine;
import org.mule.transformer.simple.SetPayloadTransformer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

public class SetPayloadConfigLineParser implements ConfigLineParser {
    @Override
    public void register(ConfigParserRegistry configParserRegistry) {
        configParserRegistry.registerParser("set-payload", this);
    }

    @Override
    public MuleBeanDefinitionHolder parse(ConfigLine configLine) {
        MuleBeanDefinitionHolder.Builder builder = new MuleBeanDefinitionHolder.Builder();
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SetPayloadTransformer.class);
        beanDefinitionBuilder.addPropertyValue("value", configLine.getRawAttributes().get("value"));
        builder.setBeanDefinition(beanDefinitionBuilder.getBeanDefinition());
        builder.setRegistrable(false);
        builder.setParentProperty("messageProcessors");
        return builder.build();
    }
}
