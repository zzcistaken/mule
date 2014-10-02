/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.bootstrap;

import org.mule.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

/**
 *
 */
public class DefaultRegistryBootstrapService implements RegistryBootstrapService
{
    private static final String BOOTSTRAP_PROPERTIES = "META-INF/services/org/mule/config/registry-bootstrap.properties";

    private final BundleContext bundleContext;

    public DefaultRegistryBootstrapService(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

    @Override
    public Properties loadProperties()
    {
        BundleWiring bundleWiring = bundleContext.getBundle().adapt(BundleWiring.class);
        ClassLoader bundleClassLoader = bundleWiring.getClassLoader();
        InputStream resourceAsStream = bundleClassLoader.getResourceAsStream(BOOTSTRAP_PROPERTIES);

        Properties properties = new Properties();

        if (resourceAsStream != null)
        {
            try
            {
                properties.load(resourceAsStream);
            }
            catch (IOException e)
            {
                //TODO(pablo.kraan): OSGi - add a better exceptoin here
                throw new RuntimeException(e);
            }
        }

        return properties;
    }

    @Override
    public Object instantiateClass(String name, Object... constructorArgs) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        return ClassUtils.instanciateClass(name, constructorArgs);
    }

    @Override
    public Class forName(String name) throws ClassNotFoundException
    {
        return Class.forName(name);
    }
}
