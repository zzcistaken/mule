/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.spring;

import org.mule.api.MuleContext;
import org.mule.api.config.MuleProperties;
import org.mule.config.ConfigResource;
import org.mule.construct.Flow;
import org.mule.util.IOUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * <code>MuleApplicationContext</code> is a simple extension application context
 * that allows resources to be loaded from the Classpath of file system using the
 * MuleBeanDefinitionReader.
 *
 */
public class MuleApplicationContext extends AbstractXmlApplicationContext
{
    private static final Log logger = LogFactory.getLog(MuleApplicationContext.class);
    private MuleContext muleContext;
    private Resource[] springResources;
    private static final ThreadLocal<MuleContext> currentMuleContext = new ThreadLocal<MuleContext>();
    /**
     * Parses configuration files creating a spring ApplicationContext which is used
     * as a parent registry using the SpringRegistry registry implementation to wraps
     * the spring ApplicationContext
     * 
     * @param configResources
     * @see org.mule.config.spring.SpringRegistry
     */
    public MuleApplicationContext(MuleContext muleContext, ConfigResource[] configResources)
            throws BeansException
    {
        this(muleContext, convert(configResources));
    }

    public MuleApplicationContext(MuleContext muleContext, Resource[] springResources) throws BeansException
    {
        this.muleContext = muleContext;
        this.springResources = springResources;
    }

    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) 
    {
        super.prepareBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(new MuleContextPostProcessor(muleContext));
        beanFactory.addBeanPostProcessor(new ExpressionEvaluatorPostProcessor(muleContext));
        beanFactory.addBeanPostProcessor(new GlobalNamePostProcessor());
        beanFactory.addBeanPostProcessor(new MergedBeanDefinitionPostProcessor()
        {
            @Override
            public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName)
            {
                if (beanDefinition.getBeanClass().equals(Flow.class))
                {
                    HashMap<String, String> templateValues = new HashMap<String, String>();
                    PropertyValue templateProperties = beanDefinition.getPropertyValues().getPropertyValue("templateProperties");
                    ManagedList managedList = (ManagedList) ((BeanDefinition) templateProperties.getValue()).getPropertyValues().getPropertyValueList().get(0).getValue();
                    for (int i = 0; i < managedList.size(); i++)
                    {
                        ManagedMap map = (ManagedMap) managedList.get(i);
                        Set set = map.keySet();
                        for (Object key : set)
                        {
                            templateValues.put((String)key,(String)map.get(key));
                        }
                    }
                    HashMap<String, BeanDefinition> templateStagesMap = new HashMap<String, BeanDefinition>();
                    PropertyValue templateStages = beanDefinition.getPropertyValues().getPropertyValue("templateStages");
                    if ((templateStages != null) && templateStages.getValue() instanceof ManagedList)
                    {
                        ManagedList stages = (ManagedList) templateStages.getValue();
                        for (int i = 0; i < stages.size(); i++)
                        {
                            templateStagesMap.put((String)((BeanDefinition)((ManagedList) templateStages.getValue()).get(i)).getPropertyValues().getPropertyValue("name").getValue(),(BeanDefinition)stages.get(i));
                        }
                    }
                    replaceTemplatePropertiesWithRealValues(beanDefinition, templateValues, templateStagesMap);
                    beanDefinition.getPropertyValues().removePropertyValue("templateStages");
                }
            }

            private void replaceTemplatePropertiesWithRealValues(BeanDefinition beanDefinition, Map<String, String> templateValues, HashMap<String, BeanDefinition> templateStagesMap)
            {
                String prefix = "#{";
                List<ExtendedPropertyValue> propertyValuesToReplace = new ArrayList<ExtendedPropertyValue>();
                for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValueList())
                {
                    Object value = propertyValue.getValue();
                    if (propertyValue instanceof ExtendedPropertyValue)
                    {
                        ExtendedPropertyValue extendedPropertyValue = (ExtendedPropertyValue) propertyValue;
                        String propertyKey = extendedPropertyValue.getPropertyExpression().substring(prefix.length(), extendedPropertyValue.getPropertyExpression().length() - 1);
                        propertyValuesToReplace.add(new ExtendedPropertyValue(propertyValue.getName(),templateValues.get(propertyKey),extendedPropertyValue.getPropertyExpression()));
                    }
                    else if (value instanceof String)
                    {
                        String valueAsString = (String) value;
                        if (valueAsString.startsWith(prefix))
                        {
                            String propertyKey = valueAsString.substring(prefix.length(), valueAsString.length() - 1);
                            //propertyValue.setConvertedValue(templateValues.get(propertyKey));
                            propertyValuesToReplace.add(new ExtendedPropertyValue(propertyValue.getName(),templateValues.get(propertyKey), (String) propertyValue.getValue()));
                        }
                    }
                    else if (value instanceof BeanDefinition)
                    {
                        replaceTemplatePropertiesWithRealValues((RootBeanDefinition) value,templateValues, templateStagesMap);
                    }
                    else if (value instanceof ManagedList)
                    {
                        ManagedList managedList = (ManagedList) value;
                        for (int i = 0; i < managedList.size(); i++)
                        {
                            if (managedList.get(i) instanceof BeanDefinition && ((BeanDefinition)managedList.get(i)).getBeanClassName().contains("TemplateStage"))
                            {
                                BeanDefinition templateStageDefinition = (BeanDefinition) managedList.remove(i);
                                managedList.add(i,templateStagesMap.get(templateStageDefinition.getPropertyValues().getPropertyValue("name").getValue()));
                            }
                            else if (managedList.get(i) instanceof BeanDefinition)
                            {
                                replaceTemplatePropertiesWithRealValues((BeanDefinition) managedList.get(i),templateValues, templateStagesMap);
                            }
                            else if (managedList.get(i) instanceof RuntimeBeanReference)
                            {
                                RuntimeBeanReference runtimeBeanReference = (RuntimeBeanReference) managedList.get(i);
                                if (runtimeBeanReference.getBeanName().startsWith(prefix))
                                {
                                    managedList.remove(i);
                                    String propertyKey = runtimeBeanReference.getBeanName().substring(prefix.length(), runtimeBeanReference.getBeanName().length() - 1);
                                    RuntimeBeanReference runtimeBeanReference1 = new MuleRuntimeBeanReference(templateValues.get(propertyKey), runtimeBeanReference.isToParent(),propertyKey);
                                    managedList.add(i, runtimeBeanReference1);
                                }
                                else if (runtimeBeanReference instanceof MuleRuntimeBeanReference)
                                {
                                    managedList.remove(i);
                                    String propertyKey = ((MuleRuntimeBeanReference)runtimeBeanReference).getTemplateKey();
                                    managedList.add(i, new MuleRuntimeBeanReference(templateValues.get(propertyKey), runtimeBeanReference.isToParent(),propertyKey));
                                }
                            }
                        }
                    }
                }
                for (ExtendedPropertyValue propertyValue : propertyValuesToReplace)
                {
                    logger.warn("BEAN>>>> " + "replacing attribute " + propertyValue.getName() + " for value " + propertyValue.getValue() );
                    beanDefinition.getPropertyValues().removePropertyValue(propertyValue.getName());
                    beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
                }
            }



            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
            {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
            {
                return bean;
            }
        });
        beanFactory.registerSingleton(MuleProperties.OBJECT_MULE_CONTEXT, muleContext);
    }

    public static class ExtendedPropertyValue extends PropertyValue
    {

        public String getPropertyExpression()
        {
            return propertyExpression;
        }

        private String propertyExpression;

        public ExtendedPropertyValue(String name, Object value, String propertyExpression)
        {
            super(name, value);
            this.propertyExpression = propertyExpression;
        }

    }

    private static Resource[] convert(ConfigResource[] resources)
    {
        Resource[] configResources = new Resource[resources.length];
        for (int i = 0; i < resources.length; i++)
        {
            ConfigResource resource = resources[i];
            if(resource.getUrl()!=null)
            {
                configResources[i] = new UrlResource(resource.getUrl());
            }
            else
            {
                try
                {
                    configResources[i] = new ByteArrayResource(IOUtils.toByteArray(resource.getInputStream()), resource.getResourceName());
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return configResources;
    }

    @Override
    protected Resource[] getConfigResources()
    {
        return springResources;
    }

    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException
    {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        //hook in our custom hierarchical reader
        beanDefinitionReader.setDocumentReaderClass(MuleBeanDefinitionDocumentReader.class);
        //add error reporting
        beanDefinitionReader.setProblemReporter(new MissingParserProblemReporter());

        // Communicate mule context to parsers
        try
        {
            currentMuleContext.set(muleContext);
            beanDefinitionReader.loadBeanDefinitions(springResources);
        }
        finally
        {
            currentMuleContext.remove();
        }
    }

    @Override
    protected DefaultListableBeanFactory createBeanFactory()
    {
        //Copy all postProcessors defined in the defaultMuleConfig so that they get applied to the child container
        DefaultListableBeanFactory bf = super.createBeanFactory();
        if (getParent() != null)
        {
            //Copy over all processors
            AbstractBeanFactory beanFactory = (AbstractBeanFactory)getParent().getAutowireCapableBeanFactory();
            bf.copyConfigurationFrom(beanFactory);
        }
        return bf;
    }

    public MuleContext getMuleContext()
    {
        return muleContext;
    }

    public static ThreadLocal<MuleContext> getCurrentMuleContext()
    {
        return currentMuleContext;
    }
}
