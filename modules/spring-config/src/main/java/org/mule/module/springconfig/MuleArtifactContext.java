/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.springconfig;

import org.mule.api.MuleContext;
import org.mule.api.config.MuleProperties;
import org.mule.config.ConfigResource;
import org.mule.util.IOUtils;

import java.io.IOException;

import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * <code>MuleArtifactContext</code> is a simple extension application context
 * that allows resources to be loaded from the Classpath of file system using the
 * MuleBeanDefinitionReader.
 *
 */
public class MuleArtifactContext extends OsgiBundleXmlApplicationContext
{
    private MuleContext muleContext;
    private Resource[] springResources;
    private static final ThreadLocal<MuleContext> currentMuleContext = new ThreadLocal<MuleContext>();
    /**
     * Parses configuration files creating a spring ApplicationContext which is used
     * as a parent registry using the SpringRegistry registry implementation to wraps
     * the spring ApplicationContext
     *
     * @param configResources
     * @param bundleContext
     * @see SpringRegistry
     */
    public MuleArtifactContext(MuleContext muleContext, ConfigResource[] configResources, BundleContext bundleContext)
            throws BeansException
    {
        this(muleContext, convert(configResources), bundleContext);
    }

    public MuleArtifactContext(MuleContext muleContext, Resource[] springResources, BundleContext bundleContext) throws BeansException
    {
        super(resourceToStrings(springResources), null);
        this.muleContext = muleContext;
        this.springResources = springResources;
        setBundleContext(bundleContext);
    }

    private static String[] resourceToStrings(Resource[] springResources)
    {
        //TODO(pablo.kraan): OSGi - makes no sense to convert to two different formats
        String[] resources = new String[springResources.length];
        int i = 0;
        for (Resource springResource : springResources)
        {
            try
            {
                resources[i++] = springResource.getURI().toString();
            }
            catch (IOException e)
            {
                throw new IllegalStateException("Unable to convert resource to string", e);
            }
        }

        return resources;
    }


    @Override
    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory)
    {
        super.prepareBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(new MuleContextPostProcessor(muleContext));
        beanFactory.addBeanPostProcessor(new ExpressionEvaluatorPostProcessor(muleContext));
        beanFactory.addBeanPostProcessor(new GlobalNamePostProcessor());
        beanFactory.registerSingleton(MuleProperties.OBJECT_MULE_CONTEXT, muleContext);
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
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException
    {
        //BeanDefinitionReader beanDefinitionReader = createBeanDefinitionReader(beanFactory);

        // Communicate mule context to parsers
        try
        {
            currentMuleContext.set(muleContext);
            super.loadBeanDefinitions(beanFactory);
        }
        finally
        {
            currentMuleContext.remove();
        }
    }

    @Override
    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {

        beanDefinitionReader.setDocumentReaderClass(MuleBeanDefinitionDocumentReader.class);
        //add error reporting
        beanDefinitionReader.setProblemReporter(new MissingParserProblemReporter());
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
