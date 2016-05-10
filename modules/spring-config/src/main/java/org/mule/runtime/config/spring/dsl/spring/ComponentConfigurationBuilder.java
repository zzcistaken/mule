/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring.dsl.spring;

import static org.mule.runtime.config.spring.dsl.model.ApplicationModel.PROCESSING_STRATEGY_ATTRIBUTE;
import static org.mule.runtime.config.spring.dsl.spring.CommonBeanDefinitionCreator.areMatchingTypes;
import static org.mule.runtime.config.spring.util.ProcessingStrategyUtils.parseProcessingStrategy;
import org.mule.runtime.config.spring.dsl.api.ComponentBuildingDefinition;
import org.mule.runtime.config.spring.dsl.api.ParameterDefinition;
import org.mule.runtime.config.spring.dsl.model.ComponentModel;
import org.mule.runtime.config.spring.dsl.processor.ParameterDefinitionVisitor;
import org.mule.runtime.core.api.processor.ProcessingStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.support.ManagedList;

/**
 * Based on the object building definition provided by {@link org.mule.runtime.config.spring.dsl.api.ComponentBuildingDefinition}
 * and the user configuration defined in {@link org.mule.runtime.config.spring.dsl.model.ComponentModel} it populates all the
 * spring {@link org.springframework.beans.factory.config.BeanDefinition} attributes using the helper class {@link org.mule.runtime.config.spring.dsl.spring.BeanDefinitionBuilderHelper}.
 */
public class ComponentConfigurationBuilder
{

    private final BeanDefinitionBuilderHelper beanDefinitionBuilderHelper;
    private final ObjectReferencePopulator objectReferencePopulator = new ObjectReferencePopulator();
    private final List<BeanValueTypePair> complexParameters;
    private final Map<String, String> simpleParameters;
    private final ComponentModel componentModel;
    private final ComponentBuildingDefinition componentBuildingDefinition;

    public ComponentConfigurationBuilder(ComponentModel componentModel,
                                         ComponentBuildingDefinition componentBuildingDefinition,
                                         BeanDefinitionBuilderHelper beanDefinitionBuilderHelper)
    {
        this.componentModel = componentModel;
        this.componentBuildingDefinition = componentBuildingDefinition;
        this.beanDefinitionBuilderHelper = beanDefinitionBuilderHelper;
        this.simpleParameters = new HashMap(componentModel.getAttributes());
        this.complexParameters = collectComplexParametersWithTypes(componentModel);
    }

    public void processConfiguration()
    {
        for (Map.Entry<String, ParameterDefinition> definitionEntry : componentBuildingDefinition.getSetterParameterDefinitions().entrySet())
        {
            definitionEntry.getValue().accept(setterVisitor(definitionEntry.getKey()));
        }
        for (ParameterDefinition parameterDefinition : componentBuildingDefinition.getConstructorParameterDefinition())
        {
            parameterDefinition.accept(constructorVisitor());
        }
    }

    private List<BeanValueTypePair> collectComplexParametersWithTypes(ComponentModel componentModel)
    {
        /*
         * TODO: MULE-9638 This ugly code is required since we need to get the object type from the bean definition.
         * This code will go away one we remove the old parsing method.
         */
        return componentModel.getInnerComponents().stream().map(cdm -> {
            //When it comes from old model it does not have the type set
            Class<?> beanDefinitionType = cdm.getType();
            if (beanDefinitionType == null)
            {
                if (cdm.getBeanDefinition() == null)
                {
                    //Some component do not have a bean definition since the element parsing is ignored. i.e: annotations
                    return null;
                }
                else
                {
                    try
                    {
                        beanDefinitionType = Class.forName(cdm.getBeanDefinition().getBeanClassName());
                    }
                    catch (ClassNotFoundException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
            Object bean = cdm.getBeanDefinition() != null ? cdm.getBeanDefinition() : cdm.getBeanReference();
            return new BeanValueTypePair(beanDefinitionType, bean);
        }).filter(beanDefinitionTypePair -> {
            return beanDefinitionTypePair != null;
        }).collect(Collectors.toList());
    }

    private ConfigurableParameterDefinitionVisitor constructorVisitor()
    {
        return new ConfigurableParameterDefinitionVisitor(beanDefinitionBuilderHelper::addConstructorValue, beanDefinitionBuilderHelper::addConstructorReference);
    }

    private ConfigurableParameterDefinitionVisitor setterVisitor(String propertyName)
    {
        return new ConfigurableParameterDefinitionVisitor(beanDefinitionBuilderHelper.forProperty(propertyName)::addValue, beanDefinitionBuilderHelper.forProperty(propertyName)::addReference);
    }

    public class ConfigurableParameterDefinitionVisitor implements ParameterDefinitionVisitor
    {

        private final Consumer<String> referencePopulator;
        private final Consumer<Object> valuePopulator;

        ConfigurableParameterDefinitionVisitor(Consumer<Object> valuePopulator, Consumer<String> referencePopulator)
        {
            this.valuePopulator = valuePopulator;
            this.referencePopulator = referencePopulator;
        }

        @Override
        public void onReferenceObject(Class<?> objectType)
        {
            objectReferencePopulator.populate(objectType, referencePopulator);
        }

        @Override
        public void onReferenceSimpleParameter(final String configAttributeName)
        {
            String reference = simpleParameters.get(configAttributeName);
            if (configAttributeName.equals(PROCESSING_STRATEGY_ATTRIBUTE))
            {
                ProcessingStrategy processingStrategy = parseProcessingStrategy(reference);
                if (processingStrategy != null)
                {
                    valuePopulator.accept(processingStrategy);
                    return;
                }
            }
            simpleParameters.remove(configAttributeName);
            if (reference != null)
            {
                referencePopulator.accept(reference);
            }
            else
            {
                valuePopulator.accept(null);
            }
        }

        @Override
        public void onFixedValue(Object value)
        {
            valuePopulator.accept(value);
        }

        @Override
        public void onConfigurationParameter(String parameterName, Object defaultValue)
        {
            Object value = simpleParameters.get(parameterName);
            simpleParameters.remove(parameterName);
            valuePopulator.accept(Optional.ofNullable(value).orElse(defaultValue));
        }

        @Override
        public void onUndefinedSimpleParameters()
        {
            valuePopulator.accept(simpleParameters);
        }

        @Override
        public void onUndefinedComplexParameters()
        {
            valuePopulator.accept(constructManagedList(fromBeanDefinitionTypePairToBeanDefinition(complexParameters)));
        }

        @Override
        public void onComplexChildList(Class<?> type)
        {
            List<BeanValueTypePair> matchingBeanValueTypePairs = complexParameters.stream().filter(beanDefinitionTypePair -> {
                return areMatchingTypes(type, beanDefinitionTypePair.getType());
            }).collect(Collectors.toList());

            matchingBeanValueTypePairs.stream().forEach(beanDefinitionTypePair -> {
                complexParameters.remove(beanDefinitionTypePair);
            });

            valuePopulator.accept(constructManagedList(fromBeanDefinitionTypePairToBeanDefinition(matchingBeanValueTypePairs)));
        }

        @Override
        public void onComplexChild(Class<?> type)
        {
            Optional<BeanValueTypePair> value = complexParameters.stream().filter(beanDefinitionTypePair -> {
                return areMatchingTypes(type, beanDefinitionTypePair.getType());
            }).findFirst();
            value.ifPresent(beanDefinitionTypePair -> {
                complexParameters.remove(beanDefinitionTypePair);
                valuePopulator.accept(beanDefinitionTypePair.getBean());
            });
        }

        @Override
        public void onValueFromTextContent()
        {
            valuePopulator.accept(componentModel.getTextContent());
        }

    }

    private ManagedList constructManagedList(List<Object> beans)
    {
        ManagedList managedList = new ManagedList();
        managedList.addAll(beans);
        return managedList;
    }

    private List<Object> fromBeanDefinitionTypePairToBeanDefinition(List<BeanValueTypePair> undefinedComplexParameters)
    {
        return undefinedComplexParameters.stream().map(beanDefinitionTypePair -> {
            return beanDefinitionTypePair.getBean();
        }).collect(Collectors.toList());
    }

}
