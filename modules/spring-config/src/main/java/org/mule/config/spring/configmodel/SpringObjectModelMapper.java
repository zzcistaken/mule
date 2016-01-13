package org.mule.config.spring.configmodel;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * Creates a {@link org.springframework.beans.factory.config.BeanDefinition} from an
 * {@link ComponentDefinitionModel}.
 */
public class SpringObjectModelMapper {

    private ModelMapperRegistry modelMapperRegistry;

    public SpringObjectModelMapper(ModelMapperRegistry modelMapperRegistry) {
        this.modelMapperRegistry = modelMapperRegistry;
    }

    public BeanDefinition convert(ComponentDefinitionModel componentDefinitionModel)
    {
        this.modelMapperRegistry.getMapper(componentDefinitionModel);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition();

        for (Attribute attribute : componentDefinitionModel.getAttributes()) {
            attribute.
        }
    }

}
