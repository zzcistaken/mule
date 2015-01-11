/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.mule.api.MuleContext;
import org.mule.api.registry.ServiceDescriptor;
import org.mule.api.registry.ServiceException;
import org.mule.api.registry.TransportDescriptorService;
import org.mule.api.registry.TransportServiceDescriptorFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

/**
 *
 */
public class TransportDescriptorServiceWrapper extends OsgiServiceWrapper implements TransportDescriptorService
{

    private final Map<String, TransportServiceDescriptorFactory> serviceDescriptorFactories = new ConcurrentHashMap<>();

    public TransportDescriptorServiceWrapper(BundleContext bundleContext)
    {
        super(bundleContext);
    }

    public static TransportDescriptorServiceWrapper createTransportDescriptorServiceWrapper(BundleContext bundleContext)
    {
        TransportDescriptorServiceWrapper transportDescriptorServiceWrapper = new TransportDescriptorServiceWrapper(bundleContext);
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
        serviceDescriptorFactories.remove(transport);
        bundleContext.ungetService(serviceReference);
    }

    @Override
    protected void doRegisterService(ServiceReference serviceReference)
    {
        TransportServiceDescriptorFactory service = (TransportServiceDescriptorFactory) bundleContext.getService(serviceReference);
        String transport = (String) serviceReference.getProperty(TransportServiceDescriptorFactory.TRANSPORT_SERVICE_TYPE);
        serviceDescriptorFactories.put(transport, service);
    }

    @Override
    public ServiceDescriptor getDescriptor(String transport, MuleContext muleContext, Properties overrides) throws ServiceException
    {
        TransportServiceDescriptorFactory transportServiceDescriptorFactory = serviceDescriptorFactories.get(transport);

        if (transportServiceDescriptorFactory == null)
        {
            throw new IllegalStateException(String.format("Unable to obtain a service descriptor for transport '%s'", transport));
        }

        return transportServiceDescriptorFactory.create(muleContext, overrides);
    }
}
