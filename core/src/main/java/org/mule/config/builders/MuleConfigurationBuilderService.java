/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.builders;

import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.config.ConfigurationException;
import org.mule.config.ConfigResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MuleConfigurationBuilderService implements ConfigurationBuilderService
{

    private final Map<String, ConfigurationBuilderFactory> configurationBuilderFactories = new HashMap<>();

    @Override
    public ConfigurationBuilder createConfigurationBuilder(String fileExtension, MuleContext domainContext, List<ConfigResource> configs) throws ConfigurationException
    {
        ConfigurationBuilderFactory configurationBuilderFactory = configurationBuilderFactories.get(fileExtension);

        if (configurationBuilderFactory == null)
        {
            throw new IllegalStateException("Unable to obtain a configuration builder factory");
        }

        return configurationBuilderFactory.createConfigurationBuilder(domainContext, configs);
    }

    @Override
    public void registerconfigurationBuilderFactory(String extension, ConfigurationBuilderFactory factory)
    {
        configurationBuilderFactories.put(extension, factory);
    }

    @Override
    public boolean unregisterDescriptorFactory(String extension)
    {
        return configurationBuilderFactories.remove(extension) != null;
    }
}
