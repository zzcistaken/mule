/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring.dsl.model;

import org.mule.runtime.core.api.processor.MessageProcessor;
import org.mule.runtime.core.api.processor.MessageRouter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

/**
 * An ComponentDefinitionModel represents the definition of a component (flow, config, message processor, etc) in the
 * mule artifact configuration.
 */
public class ComponentModel
{

    private boolean root = false;
    private ComponentIdentifier identifier;
    private Map<String, Object> customAttributes = new HashMap<>();
    private Map<String, String> attributes = new HashMap<>();
    private List<ComponentModel> innerComponents = new ArrayList<>();
    private String textContent;
    private BeanReference beanReference;
    //TODO remove once the old parsing mechanism is not needed anymore
    public ComponentModel()
    {
    }

    //TODO remove this attributes
    private BeanDefinition beanDefinition;
    private Class<?> type;

    public ComponentIdentifier getIdentifier() {
        return identifier;
    }

    public Map<String, String> getAttributes()
    {
        return Collections.unmodifiableMap(attributes);
    }

    public List<ComponentModel> getInnerComponents() {
        return innerComponents;
    }

    public List<ComponentModel> getInnerMessageProcessorModels() {
        return innerComponents.stream().filter(component -> MessageProcessor.class.isAssignableFrom(component.getType())).collect(Collectors.toList());
    }

    public Map<String, Object> getCustomAttributes() {
        return Collections.unmodifiableMap(customAttributes);
    }

    public boolean isRoot() {
        return root;
    }

    public void setBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    public BeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getNameAttribute() {
        return attributes.get("name");
    }

    public boolean isScope() {
        return MessageRouter.class.isAssignableFrom(type);
    }

    public String getTextContent()
    {
        return textContent;
    }

    public void setBeanReference(BeanReference beanReference)
    {
        this.beanReference = beanReference;
    }

    public BeanReference getBeanReference()
    {
        return beanReference;
    }

    public static class Builder
    {

        private ComponentModel model = new ComponentModel();

        public Builder setIdentifier(ComponentIdentifier identifier) {
            this.model.identifier = identifier;
            return this;
        }

        public Builder addAttribute(String key, String value) {
            this.model.attributes.put(key, value);
            return this;
        }

        public Builder addChildComponentDefinitionModel(ComponentModel componentModel) {
            this.model.innerComponents.add(componentModel);
            return this;
        }

        public Builder setTextContent(String textContent)
        {
            this.model.textContent = textContent;
            return this;
        }

        public Builder markAsRootComponent()
        {
            this.model.root = true;
            return this;
        }

        public Builder addCustomAttribute(String name, Object value)
        {
            this.model.customAttributes.put(name, value);
            return this;
        }

        public ComponentModel build()
        {
            return model;
        }

    }

    public Builder builderCopy()
    {
        List<ComponentModel> childrenCopies = copy(innerComponents);
        Builder builder = new Builder();
        builder.setIdentifier(this.identifier);
        for (ComponentModel childrenCopy : childrenCopies)
        {
            builder.addChildComponentDefinitionModel(childrenCopy);
        }
        for (Map.Entry<String, String> entry : attributes.entrySet())
        {
            builder.addAttribute(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    /**
     * deep copy
     * @return
     */
    public ComponentModel copy()
    {
        return builderCopy().build();
    }

    private List<ComponentModel> copy(List<ComponentModel> componentModels)
    {
        List<ComponentModel> copies = new ArrayList<>();
        for (ComponentModel componentModel : componentModels)
        {
            copies.add(componentModel.copy());
        }
        return copies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentModel that = (ComponentModel) o;

        if (root != that.root) return false;
        if (!identifier.equals(that.identifier)) return false;
        if (!attributes.equals(that.attributes)) return false;
        return innerComponents.equals(that.innerComponents);

    }

    /**
     * Used to determine if a top level config component is the same as
     * another by name
     * @param o another model
     * @return true if both models have the same name
     * //TODO shouldn't this also compare by the identifier and namespace?
     */
    public int equalsById(Object o) {
        if (this == o) return -1;
        if (o == null || getClass() != o.getClass()) return -1;

        ComponentModel that = (ComponentModel) o;

        return attributes.get("name").compareTo(that.attributes.get("name"));

    }

    @Override
    public int hashCode() {
        int result = (root ? 1 : 0);
        result = 31 * result + identifier.hashCode();
        result = 31 * result + attributes.hashCode();
        result = 31 * result + innerComponents.hashCode();
        return result;
    }

}
