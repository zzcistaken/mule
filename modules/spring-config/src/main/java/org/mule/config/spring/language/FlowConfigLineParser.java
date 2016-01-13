package org.mule.config.spring.language;

import org.mule.api.config.MuleProperties;
import org.mule.api.lang.ConfigLine;
import org.mule.construct.Flow;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

public class FlowConfigLineParser implements ConfigLineParser {


    @Override
    public void register(ConfigParserRegistry configParserRegistry) {
        configParserRegistry.registerParser("flow", this);
    }

    @Override
    public MuleBeanDefinitionHolder parse(ConfigLine configLine) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Flow.class);
        String flowName = configLine.getRawAttributes().get("name");
        beanDefinitionBuilder.addConstructorArgValue(flowName);
        beanDefinitionBuilder.addConstructorArgReference(MuleProperties.OBJECT_MULE_CONTEXT);
        MuleBeanDefinitionHolder.Builder builder = new MuleBeanDefinitionHolder.Builder();
        builder.setBeanDefinition(beanDefinitionBuilder.getBeanDefinition());
        builder.setRegistrable(true);
        builder.setRegistryName(flowName);
        //TODO add
//        ProcessingStrategyUtils.configureProcessingStrategy(
//                ProcessingStrategyUtils.QUEUED_ASYNC_PROCESSING_STRATEGY);
        return builder.build();
    }
}
