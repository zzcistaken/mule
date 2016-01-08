/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.deployer.extension;

import org.mule.MuleCoreExtension;
import org.mule.api.DefaultMuleException;
import org.mule.util.ClassUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

/**
 * Discovers {@link MuleCoreExtension} classes that are defined in the
 *  classpath using core-extensions.properties files.
 */
public class OsgiCoreExtensionDiscoverer implements MuleCoreExtensionDiscoverer
{

    public static final String SERVICE_PATH = "META-INF/services/org/mule/config/";
    public static final String CORE_EXTENSION_PROPERTIES = "core-extensions.properties";

    private static Log logger = LogFactory.getLog(OsgiCoreExtensionDiscoverer.class);
    private final BundleContext bundleContext;

    public OsgiCoreExtensionDiscoverer(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

    @Override
    public List<MuleCoreExtension> discover() throws DefaultMuleException
    {
        List<MuleCoreExtension> result = new LinkedList<MuleCoreExtension>();

        for (Bundle bundle : bundleContext.getBundles())
        {
            //TODO(pablo.kraan): OSGi - should considered only started bundles?
            if (bundle.getState() == Bundle.ACTIVE)
            {
                Enumeration coreExtensionFiles = bundle.findEntries(SERVICE_PATH, CORE_EXTENSION_PROPERTIES, false);
                while (coreExtensionFiles != null && coreExtensionFiles.hasMoreElements())
                {
                    Properties properties = new Properties();

                    try
                    {
                        URL url = (URL) coreExtensionFiles.nextElement();
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Reading extension file: " + url.toString());
                        }
                        properties.load(url.openStream());
                    }
                    catch (Exception ex)
                    {
                        throw new DefaultMuleException("Error loading Mule core extensions", ex);
                    }


                    //BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
                    //ClassLoader bundleClassLoader = bundleWiring.getClassLoader();
                    //
                    ////TODO(pablo.kraan): OSGi - need to track services in order to remove them when the bundles are unregistered
                    //BootstrapService service = new PropertiesBootstrapService(bundleClassLoader, properties);
                    //bundleContext.registerService(BootstrapService.class.getName(), service, null);
                    for (Map.Entry entry : properties.entrySet())
                    {
                        String extName = (String) entry.getKey();
                        String extClass = (String) entry.getValue();
                        try
                        {
                            BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
                            ClassLoader bundleClassLoader = bundleWiring.getClassLoader();

                            MuleCoreExtension extension = (MuleCoreExtension) ClassUtils.instanciateClass(extClass, new Object[0], bundleClassLoader);
                            result.add(extension);
                        }
                        catch (Exception ex)
                        {
                            throw new DefaultMuleException("Error starting Mule core extension " + extName, ex);
                        }
                    }
                }
            }
        }

        return result;
    }
}
