/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.example.osgi;

import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.context.MuleContextBuilder;
import org.mule.config.PropertiesMuleConfigurationFactory;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.module.springconfig.SpringXmlConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 */
public class ExampleActivator implements BundleActivator
{
    private MuleContext muleContext;

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        System.out.println("Starting Example bundle ");
        //TODO(pablo.kraan): OSGi - setting property to see full exceptions
        System.setProperty("mule.verbose.exceptions", "true");

        try
        {
            String configResource = "mule-config.xml";

            SpringXmlConfigurationBuilder cfgBuilder = new SpringXmlConfigurationBuilder(configResource);

            //TODO(pablo.kraan): add the rest of the original configuration builders
            List<ConfigurationBuilder> configBuilders = new ArrayList<ConfigurationBuilder>(1);

            // need to add the annotations config builder before Spring so we can use Mule
            // annotations in Spring
            //addAnnotationsConfigBuilder(configBuilders);
            //addStartupPropertiesConfigBuilder(configBuilders);
            configBuilders.add(cfgBuilder);

            MuleConfiguration configuration = createMuleConfiguration(configResource);

            MuleContextBuilder contextBuilder = new DefaultMuleContextBuilder();
            contextBuilder.setMuleConfiguration(configuration);

            DefaultMuleContextFactory contextFactory = new DefaultMuleContextFactory();
            contextFactory.setBundleContext(bundleContext);
            contextFactory.setClassLoader(this.getClass().getClassLoader());
            muleContext = contextFactory.createMuleContext(configBuilders, contextBuilder);
            muleContext.start();
        }
        catch (Throwable e)
        {
            System.out.println("Error starting Example bundle: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Example bundle started");

    }

    protected MuleConfiguration createMuleConfiguration(String appConfigurationResource)
    {
        String appPropertiesFile;

        if (appConfigurationResource == null)
        {
            appPropertiesFile = PropertiesMuleConfigurationFactory.getMuleAppConfiguration(appConfigurationResource);
        }
        else
        {
            appPropertiesFile = appConfigurationResource;
        }

        return new PropertiesMuleConfigurationFactory(appPropertiesFile).createConfiguration();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        System.out.println("Stopping Example bundle");
        if (muleContext != null)
        {
            muleContext.stop();
        }
        System.out.println("Example bundle stopped");
    }
}
