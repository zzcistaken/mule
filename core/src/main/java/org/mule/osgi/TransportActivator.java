/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.mule.config.bootstrap.BootstrapPropertiesService;
import org.mule.config.bootstrap.MuleBootstrapPropertiesService;
import org.mule.config.bootstrap.RegistryBootstrapServiceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 */
public class TransportActivator implements BundleActivator
{

    private List<ServiceRegistration> descriptorRefs = new ArrayList<>();
    private ServiceRegistration<?> bootstrapService;

    public void start(BundleContext bundleContext) throws Exception
    {
        descriptorRefs = new BundleContextTransportServiceDescriptorFactoryDiscoverer(bundleContext.getBundle(), new OsgiTransportServiceDescriptorFactoryFactory(bundleContext.getBundle())).discover();

        URL resource = bundleContext.getBundle().getResource(RegistryBootstrapServiceUtil.BOOTSTRAP_PROPERTIES);
        if (resource != null)
        {
            Properties properties = new Properties();
            properties.load(resource.openStream());
            BootstrapPropertiesService service = new MuleBootstrapPropertiesService(properties);
            bootstrapService = bundleContext.registerService(BootstrapPropertiesService.class.getName(), service, null);
        }
    }

    public void stop(BundleContext bc) throws Exception
    {
        for (ServiceRegistration descriptorRef : descriptorRefs)
        {
            try
            {
                descriptorRef.unregister();
            }
            catch (IllegalStateException | IllegalArgumentException e)
            {
                //TODO(pablo.kraan): OSGi - add logging
                // Ignore and continue
            }

        }

        if (bootstrapService != null)
        {
            bootstrapService.unregister();
        }
    }

}
