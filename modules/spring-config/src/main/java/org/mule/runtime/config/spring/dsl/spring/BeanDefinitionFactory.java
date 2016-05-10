/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring.dsl.spring;

import static org.mule.runtime.config.spring.dsl.model.ApplicationModel.DESCRIPTION_ELEMENT;
import static org.mule.runtime.config.spring.dsl.model.ApplicationModel.MULE_ROOT_ELEMENT;
import static org.mule.runtime.config.spring.dsl.model.ApplicationModel.NAME_ATTRIBUTE;
import static org.mule.runtime.config.spring.dsl.processor.xml.CoreXmlNamespaceInfoProvider.CORE_NAMESPACE_NAME;
import static org.mule.runtime.config.spring.dsl.processor.xml.XmlCustomAttributeHandler.from;
import static org.mule.runtime.config.spring.dsl.spring.CommonBeanDefinitionCreator.adaptFilterBeanDefinitions;
import org.mule.runtime.config.spring.dsl.api.ComponentBuildingDefinition;
import org.mule.runtime.config.spring.dsl.model.ComponentBuildingDefinitionRegistry;
import org.mule.runtime.config.spring.dsl.model.ComponentIdentifier;
import org.mule.runtime.config.spring.dsl.model.ComponentModel;
import org.mule.runtime.core.api.MuleRuntimeException;
import org.mule.runtime.core.config.i18n.CoreMessages;

import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.w3c.dom.Element;

public class BeanDefinitionFactory
{
    private final ImmutableSet<ComponentIdentifier> ignoredMuleCoreComponentIdentifiers = ImmutableSet.<ComponentIdentifier>builder()
            .add(new ComponentIdentifier.Builder().withNamespace(CORE_NAMESPACE_NAME).withName(MULE_ROOT_ELEMENT).build())
            .add(new ComponentIdentifier.Builder().withNamespace(CORE_NAMESPACE_NAME).withName(DESCRIPTION_ELEMENT).build())
            .build();

    private ComponentBuildingDefinitionRegistry componentBuildingDefinitionRegistry;
    private BeanDefinitionCreator componentModelProcessor;

    public BeanDefinitionFactory(ComponentBuildingDefinitionRegistry componentBuildingDefinitionRegistry)
    {
        this.componentBuildingDefinitionRegistry = componentBuildingDefinitionRegistry;
        this.componentModelProcessor = buildComponentModelProcessorChainOfResponsability();
    }

    public BeanDefinition resolveComponentRecursively(ComponentModel parentComponentModel, ComponentModel componentModel, BeanDefinitionRegistry registry, BiConsumer<ComponentModel, BeanDefinitionRegistry> resolvedComponentDefinitionModelProcessor, BiFunction<Element, BeanDefinition, BeanDefinition> oldParsingMechansim) {
        List<ComponentModel> innerComponents = componentModel.getInnerComponents();
        if (!innerComponents.isEmpty())
        {
            for (ComponentModel innerComponent : innerComponents) {
                if (hasDefinition(innerComponent.getIdentifier()))
                {
                    resolveComponentRecursively(componentModel, innerComponent, registry, resolvedComponentDefinitionModelProcessor, oldParsingMechansim);
                }
                else
                {
                    AbstractBeanDefinition oldBeanDefinition = (AbstractBeanDefinition) oldParsingMechansim.apply((Element) from(innerComponent).getNode(), null);
                    oldBeanDefinition = adaptFilterBeanDefinitions(componentModel, oldBeanDefinition);
                    innerComponent.setBeanDefinition(oldBeanDefinition);
                }
            }
        }
        return resolveComponent(parentComponentModel, componentModel, registry, resolvedComponentDefinitionModelProcessor);
    }

    private BeanDefinition resolveComponent(ComponentModel parentComponentModel, ComponentModel componentModel, BeanDefinitionRegistry registry, BiConsumer<ComponentModel, BeanDefinitionRegistry> componentDefinitionModelProcessor) {
        if (ignoredMuleCoreComponentIdentifiers.contains(componentModel.getIdentifier()))
        {
            return null;
        }
        resolveComponentBeanDefinition(parentComponentModel, componentModel);
        componentDefinitionModelProcessor.accept(componentModel, registry);
        return componentModel.getBeanDefinition();
    }


    public void resolveComponentBeanDefinition(ComponentModel parentComponentModel, ComponentModel componentModel) {
        ComponentBuildingDefinition componentBuildingDefinition = componentBuildingDefinitionRegistry.getBuildingDefinition(componentModel.getIdentifier()).orElseThrow( () -> {
            return new MuleRuntimeException(CoreMessages.createStaticMessage(String.format("No component building definition for element %s. It may be that there's a dependency missing to the project that handle that extension.", componentModel.getIdentifier())));
        });

        ////TODO see if this condition is needed
        //if (componentDefinitionModel.getNamespace().equals(SPRING_NAMESPACE) || componentDefinitionModel.getNamespace().equals(SPRING_CONTEXT_NAMESPACE))
        //{
        //    return;
        //}
        //
        //if (componentBuildingDefinition == null)
        //{
        //    //Parse using old method
        //    BeanDefinition beanDefinition = oldParsingMechansim.apply((Element) componentDefinitionModel.getNode(), null);
        //    beanDefinition = wrapBeanDefinitionForFilters(componentDefinitionModel.getNode().getParentNode(), beanDefinition);
        //    componentDefinitionModel.setBeanDefinition(beanDefinition);
        //    return;
        //}
        this.componentModelProcessor.processRequest(new CreateBeanDefinitionRequest(parentComponentModel, componentModel, componentBuildingDefinition));
    }

    public static void checkElementNameUnique(BeanDefinitionRegistry registry, Element element)
    {
        if (null != element.getAttributeNode(NAME_ATTRIBUTE))
        {
            String name = element.getAttribute(NAME_ATTRIBUTE);
            if (registry.containsBeanDefinition(name))
            {
                throw new IllegalArgumentException("A component named " + name + " already exists.");
            }
        }
    }



    private BeanDefinitionCreator buildComponentModelProcessorChainOfResponsability()
    {
        ReferenceProcessorBeanDefinitionCreator referenceProcessorBeanDefinitionCreator = new ReferenceProcessorBeanDefinitionCreator();
        ExceptionStrategyRefBeanDefinitionCreator exceptionStrategyRefBeanDefinitionCreator = new ExceptionStrategyRefBeanDefinitionCreator();
        FilterReferenceBeanDefinitionCreator filterReferenceBeanDefinitionCreator = new FilterReferenceBeanDefinitionCreator();
        CommonBeanDefinitionCreator commonComponentModelProcessor = new CommonBeanDefinitionCreator();
        referenceProcessorBeanDefinitionCreator.setSuccessor(exceptionStrategyRefBeanDefinitionCreator);
        exceptionStrategyRefBeanDefinitionCreator.setSuccessor(exceptionStrategyRefBeanDefinitionCreator);
        exceptionStrategyRefBeanDefinitionCreator.setSuccessor(filterReferenceBeanDefinitionCreator);
        filterReferenceBeanDefinitionCreator.setSuccessor(commonComponentModelProcessor);
        return referenceProcessorBeanDefinitionCreator;
    }

    public boolean hasDefinition(ComponentIdentifier componentIdentifier)
    {
        return ignoredMuleCoreComponentIdentifiers.contains(componentIdentifier) || componentBuildingDefinitionRegistry.getBuildingDefinition(componentIdentifier).isPresent();
    }

}
