/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.vm;

import org.mule.api.registry.TransportDescriptorService;
import org.mule.api.registry.TransportServiceDescriptorFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 *
 */
public class TransportDescriptorServiceWrapper implements ServiceListener
{

    private final BundleContext bundleContext;
    private final TransportDescriptorService transportDescriptorService;

    public TransportDescriptorServiceWrapper(BundleContext bundleContext, TransportDescriptorService transportDescriptorService)
    {
        this.bundleContext = bundleContext;
        this.transportDescriptorService = transportDescriptorService;
    }

    @Override
    public void serviceChanged(ServiceEvent event)
    {
        ServiceReference serviceReference = event.getServiceReference();
        switch (event.getType())
        {
            case ServiceEvent.REGISTERED:
            {
                registerService(serviceReference);
            }
            break;
            case ServiceEvent.UNREGISTERING:
            {
                unregisterService(serviceReference);
            }
            break;
        }

    }

    private void unregisterService(ServiceReference serviceReference)
    {
        String transport = (String) serviceReference.getProperty(TransportServiceDescriptorFactory.TRANSPORT_SERVICE_TYPE);
        transportDescriptorService.unregisterDescriptorFactory(transport);
        bundleContext.ungetService(serviceReference);
    }

    private void registerService(ServiceReference serviceReference)
    {
        TransportServiceDescriptorFactory service = (TransportServiceDescriptorFactory) bundleContext.getService(serviceReference);
        String transport = (String) serviceReference.getProperty(TransportServiceDescriptorFactory.TRANSPORT_SERVICE_TYPE);
        transportDescriptorService.registerDescriptorFactory(transport, service);
    }
}
