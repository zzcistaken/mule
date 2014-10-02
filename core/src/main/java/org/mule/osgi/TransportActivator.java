/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.mule.config.bootstrap.DefaultRegistryBootstrapService;
import org.mule.config.bootstrap.RegistryBootstrapService;

import java.util.ArrayList;
import java.util.List;

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
        descriptorRefs = new BundleContextTransportServiceDescriptorFactoryDiscoverer(bundleContext).discover();

        bootstrapService = bundleContext.registerService(RegistryBootstrapService.class.getName(), new DefaultRegistryBootstrapService(bundleContext), null);
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
