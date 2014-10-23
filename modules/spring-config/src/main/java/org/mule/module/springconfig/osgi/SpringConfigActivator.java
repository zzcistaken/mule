/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.springconfig.osgi;

import org.mule.config.builders.ConfigurationBuilderFactory;
import org.mule.module.springconfig.SpringXmlConfigurationBuilderFactory;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 */
public class SpringConfigActivator implements BundleActivator
{

    @Override
    public void start(BundleContext context) throws Exception
    {
        Dictionary<String, String> properties = new Hashtable<>();
        properties.put(ConfigurationBuilderFactory.EXTENSION, "xml");

        context.registerService(
                ConfigurationBuilderFactory.class.getName(),
                new SpringXmlConfigurationBuilderFactory(), properties);
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {

    }
}
