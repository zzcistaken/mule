package org.mule.config.spring;

import org.mule.api.lang.ConfigLine;
import org.mule.config.spring.language.ApplicationConfig;
import org.mule.config.spring.language.ConfigParserRegistry;
import org.mule.config.spring.language.MuleBeanDefinitionHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.List;

public class MuleObjectCreationBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private final ApplicationConfig applicationConfig;
    private final ConfigParserRegistry configParserRegistry;

    public MuleObjectCreationBeanDefinitionRegistryPostProcessor(ApplicationConfig applicationConfig, ConfigParserRegistry configParserRegistry) {
        this.applicationConfig= applicationConfig;
        this.configParserRegistry = configParserRegistry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        for (ConfigLine configFile : applicationConfig.getAllConfigLines()) {
            List<MuleBeanDefinitionHolder> beanDefinitions = configParserRegistry.parse(configFile);
            for (MuleBeanDefinitionHolder beanDefinition : beanDefinitions) {
                if (beanDefinition.isRegistrable())
                {
                    registry.registerBeanDefinition(beanDefinition.getRegistryName(), beanDefinition.getBeanDefinition());
                }
            }
        }
    }
}
