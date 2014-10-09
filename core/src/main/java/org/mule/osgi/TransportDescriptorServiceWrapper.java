/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.mule.api.registry.MuleTransportDescriptorService;
import org.mule.api.registry.TransportDescriptorService;
import org.mule.api.registry.TransportServiceDescriptorFactory;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

/**
 *
 */
public class TransportDescriptorServiceWrapper extends OsgiServiceWrapper
{

    private final TransportDescriptorService transportDescriptorService;

    public TransportDescriptorServiceWrapper(BundleContext bundleContext, TransportDescriptorService transportDescriptorService)
    {
        super(bundleContext);
        this.transportDescriptorService = transportDescriptorService;
    }

    public static TransportDescriptorServiceWrapper createTransportDescriptorServiceWrapper(MuleTransportDescriptorService transportDescriptorService, BundleContext bundleContext)
    {
        TransportDescriptorServiceWrapper transportDescriptorServiceWrapper = new TransportDescriptorServiceWrapper(bundleContext, transportDescriptorService);
        try
        {
            String filter = "(objectclass=" + TransportServiceDescriptorFactory.class.getName() + ")";
            bundleContext.addServiceListener(transportDescriptorServiceWrapper, filter);

            Collection<ServiceReference<TransportServiceDescriptorFactory>> serviceReferences = bundleContext.getServiceReferences(TransportServiceDescriptorFactory.class, null);

            for (ServiceReference<TransportServiceDescriptorFactory> serviceReference : serviceReferences)
            {
                transportDescriptorServiceWrapper.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, serviceReference));

            }
        }
        catch (InvalidSyntaxException e)
        {
            throw new IllegalStateException(e);
        }

        return transportDescriptorServiceWrapper;
    }

    @Override
    protected void doUnregisterService(ServiceReference serviceReference)
    {
        String transport = (String) serviceReference.getProperty(TransportServiceDescriptorFactory.TRANSPORT_SERVICE_TYPE);
        transportDescriptorService.unregisterDescriptorFactory(transport);
        bundleContext.ungetService(serviceReference);
    }

    @Override
    protected void doRegisterService(ServiceReference serviceReference)
    {
        TransportServiceDescriptorFactory service = (TransportServiceDescriptorFactory) bundleContext.getService(serviceReference);
        String transport = (String) serviceReference.getProperty(TransportServiceDescriptorFactory.TRANSPORT_SERVICE_TYPE);
        transportDescriptorService.registerDescriptorFactory(transport, service);
    }
}
