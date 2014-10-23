/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.springconfig.osgi;

import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.registry.MuleTransportDescriptorService;
import org.mule.config.PropertiesMuleConfigurationFactory;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.module.springconfig.SpringXmlConfigurationBuilder;
import org.mule.osgi.TransportDescriptorServiceWrapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class MuleApplicationActivator implements BundleActivator
{

    protected transient Log logger = LogFactory.getLog(MuleApplicationActivator.class);

    //TODO(pablo.kraan): OSGi - move this class to another package/module
    private MuleContext muleContext;
    private TransportDescriptorServiceWrapper transportDescriptorServiceWrapper;

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        logger.info("Starting application:" + bundleContext.getBundle().getSymbolicName());

        //TODO(pablo.kraan): OSGi - setting property to see full exceptions
        System.setProperty("mule.verbose.exceptions", "true");

        try
        {
            String configResource = "mule-config.xml";

            SpringXmlConfigurationBuilder cfgBuilder = new SpringXmlConfigurationBuilder(configResource, bundleContext);

            //TODO(pablo.kraan): add the rest of the original configuration builders
            List<ConfigurationBuilder> configBuilders = new ArrayList<ConfigurationBuilder>(1);

            // need to add the annotations config builder before Spring so we can use Mule
            // annotations in Spring
            //addAnnotationsConfigBuilder(configBuilders);
            //addStartupPropertiesConfigBuilder(configBuilders);
            configBuilders.add(cfgBuilder);

            //TODO(pablo.kraan): OSGi - need to register all the service wrappers to registering services (like TransportDescriptorServiceWrapper)
            MuleConfiguration configuration = createMuleConfiguration(configResource);

            MuleTransportDescriptorService muleTransportDescriptorService = new MuleTransportDescriptorService();
            transportDescriptorServiceWrapper = TransportDescriptorServiceWrapper.createTransportDescriptorServiceWrapper(muleTransportDescriptorService, bundleContext);

            DefaultMuleContextBuilder contextBuilder = new DefaultMuleContextBuilder();
            contextBuilder.setMuleConfiguration(configuration);
            contextBuilder.setTransportDescriptorService(muleTransportDescriptorService);

            DefaultMuleContextFactory contextFactory = new DefaultMuleContextFactory();
            contextFactory.setBundleContext(bundleContext);
            contextFactory.setTransportDescriptorService(muleTransportDescriptorService);

            muleContext = contextFactory.createMuleContext(configBuilders, contextBuilder);
            muleContext.start();
            logger.info("Application started: " + bundleContext.getBundle().getSymbolicName());
        }
        catch (Throwable e)
        {
            System.out.println("Error starting Example bundle: " + e.getMessage());
            e.printStackTrace();
        }
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

        if (transportDescriptorServiceWrapper != null)
        {
            bundleContext.removeServiceListener(transportDescriptorServiceWrapper);
        }

        if (muleContext != null)
        {
            muleContext.stop();
        }
        System.out.println("Example bundle stopped");
    }
}
