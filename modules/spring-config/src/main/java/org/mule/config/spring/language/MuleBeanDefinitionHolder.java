package org.mule.config.spring.language;

import org.springframework.beans.factory.config.BeanDefinition;

public class MuleBeanDefinitionHolder {

    private BeanDefinition beanDefinition;
    private String registryName;
    private boolean registrable;
    private String parentProperty;

    public BeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    /**
     * @return name to use to register the object
     */
    public String getRegistryName() {
        return registryName;
    }

    /**
     * @return true if it should be registerd, false otherwise
     */
    public boolean isRegistrable() {
        return registrable;
    }

    /**
     * @return name of the parent bean definition property to place this one as child
     */
    public String getParentProperty() {
        return parentProperty;
    }

    public static class Builder {

        private MuleBeanDefinitionHolder muleBeanDefinitionHolder = new MuleBeanDefinitionHolder();

        public Builder setBeanDefinition(BeanDefinition beanDefinition) {
            muleBeanDefinitionHolder.beanDefinition = beanDefinition;
            return this;
        }

        public Builder setRegistryName(String registryName) {
            muleBeanDefinitionHolder.registryName = registryName;
            return this;
        }

        public Builder setRegistrable(boolean registrable) {
            muleBeanDefinitionHolder.registrable = registrable;
            return this;
        }

        public Builder setParentProperty(String parentProperty) {
            muleBeanDefinitionHolder.parentProperty = parentProperty;
            return this;
        }

        public MuleBeanDefinitionHolder build()
        {
            return muleBeanDefinitionHolder;
        }
    }
}
