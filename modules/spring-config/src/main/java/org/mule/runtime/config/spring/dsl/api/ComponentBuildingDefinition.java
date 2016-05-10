/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring.dsl.api;

import org.mule.runtime.config.spring.dsl.processor.TypeDefinition;
import org.mule.runtime.config.spring.dsl.model.ComponentIdentifier;
import org.mule.runtime.core.util.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the mapping between a component configuration and how the object that represents
 * that model in runtime is created.
 */
public class ComponentBuildingDefinition
{

    private TypeDefinition typeDefinition;
    private boolean scope;
    private List<ParameterDefinition> constructorParameterDefinition = new ArrayList<>();
    private Map<String, ParameterDefinition> setterParameterDefinitions = new HashMap<>();
    //TODO MULE-9638 Use generics. Generics cannot be used right now because this method colides with the ones defined in FactoryBeans.
    private Class<?> objectFactoryType;
    private boolean prototype;
    private ComponentIdentifier componentIdentifier;

    private ComponentBuildingDefinition()
    {
    }

    /**
     * @return a definition for the object type that must be created for this component
     */
    public TypeDefinition getTypeDefinition()
    {
        return typeDefinition;
    }

    /**
     * @return true if the building definition is an scope of message processors
     */
    public boolean isScope()
    {
        return scope;
    }

    /**
     * @return an ordered list of the constructor parameters that must be set to create the domain object
     */
    public List<ParameterDefinition> getConstructorParameterDefinition()
    {
        return constructorParameterDefinition;
    }

    /**
     * @return a map of the attributes that may contain configuration for the domain object to be created. The map key is the attribute name.
     */
    public Map<String, ParameterDefinition> getSetterParameterDefinitions()
    {
        return setterParameterDefinitions;
    }

    /**
     * @return the factory for the domain object. For complex object creations it's possible to define an object builder that will end up creating the domain object.
     */
    public Class<?> getObjectFactoryType()
    {
        return objectFactoryType;
    }

    /**
     * @return if the object is a prototype or a singleton
     */
    //TODO MULE-9681: remove for some other semantic. The API should not define something as "prototype" it should declare if it's a reusable component or an instance.
    //Ideally this can be inferred by the language itself. e.g.: Global message processors are always reusable components and do not define entities by them self.
    public boolean isPrototype()
    {
        return prototype;
    }

    /**
     * @return the unique identifier for this component
     */
    public ComponentIdentifier getComponentIdentifier()
    {
        return componentIdentifier;
    }

    /**
     * Builder for {@code ComponentBuildingDefinition}
     */
    public static class Builder
    {
        private String namespace;
        private String identifier;
        private ComponentBuildingDefinition definition = new ComponentBuildingDefinition();

        /**
         * Adds a new constructor parameter to be used during the object instantiation.
         *
         * @param parameterDefinition the constructor argument definition.
         * @return the builder
         */
        public Builder withConstructorParameterDefinition(ParameterDefinition parameterDefinition)
        {
            definition.constructorParameterDefinition.add(parameterDefinition);
            return this;
        }

        /**
         * Adds a new parameter to be added to the object by using a setter method.
         *
         * @param fieldName the name of the field in which the value must be injected
         * @param parameterDefinition the setter parameter definition
         * @return the builder
         */
        public Builder withSetterParameterDefinition(String fieldName, ParameterDefinition parameterDefinition)
        {
            definition.setterParameterDefinitions.put(fieldName, parameterDefinition);
            return this;
        }

        public Builder withIdentifier(String identifier)
        {
            this.identifier = identifier;
            return this;
        }

        public Builder withNamespace(String namespace)
        {
            this.namespace = namespace;
            return this;
        }

        public Builder withTypeDefinition(TypeDefinition typeDefinition)
        {
            definition.typeDefinition = typeDefinition;
            return this;
        }

        public Builder asScope()
        {
            definition.scope = true;
            return this;
        }

        public ComponentBuildingDefinition build()
        {
            Preconditions.checkState(definition.typeDefinition != null, "You must specify the type");
            Preconditions.checkState(identifier != null, "You must specify the identifier");
            Preconditions.checkState(namespace != null, "You must specify the namespace");
            definition.componentIdentifier = new ComponentIdentifier.Builder().withName(identifier).withNamespace(namespace).build();
            return definition;
        }

        public Builder withObjectFactoryType(Class<?> objectFactoryType)
        {
            definition.objectFactoryType = objectFactoryType;
            return this;
        }

        public Builder copy()
        {
            Builder builder = new Builder();
            builder.definition.setterParameterDefinitions = new HashMap<>(this.definition.setterParameterDefinitions);
            builder.definition.constructorParameterDefinition = new ArrayList<>(this.definition.constructorParameterDefinition);
            builder.identifier = this.identifier;
            builder.namespace = this.namespace;
            builder.definition.scope = this.definition.scope;
            builder.definition.typeDefinition = this.definition.typeDefinition;
            return builder;
        }

        //TODO MULE-9681: remove for some other semantic. The API should not define something as "prototype" it should declare if it's a reusable component or an instance.
        //Ideally this can be inferred by the language itself. e.g.: Global message processors are always reusable components and do not define entities by them self.
        public Builder asPrototype()
        {
            definition.prototype = true;
            return this;
        }
    }
}
