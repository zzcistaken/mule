/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.mule.config.builders.ConfigurationBuilderFactory;
import org.mule.config.builders.ConfigurationBuilderService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 *
 */
public class ConfigurationBuilderServiceWrapper extends OsgiServiceWrapper
{

    private final ConfigurationBuilderService configurationBuilderService;

    public ConfigurationBuilderServiceWrapper(BundleContext bundleContext, ConfigurationBuilderService configurationBuilderService)
    {
        super(bundleContext);

        this.configurationBuilderService = configurationBuilderService;
    }

    @Override
    protected void doUnregisterService(ServiceReference serviceReference)
    {
        String extension = (String) serviceReference.getProperty(ConfigurationBuilderFactory.EXTENSION);
        configurationBuilderService.unregisterDescriptorFactory(extension);
        bundleContext.ungetService(serviceReference);
    }

    @Override
    protected void doRegisterService(ServiceReference serviceReference)
    {
        ConfigurationBuilderFactory service = (ConfigurationBuilderFactory) bundleContext.getService(serviceReference);
        String extension = (String) serviceReference.getProperty(ConfigurationBuilderFactory.EXTENSION);
        configurationBuilderService.registerconfigurationBuilderFactory(extension, service);
    }
}
