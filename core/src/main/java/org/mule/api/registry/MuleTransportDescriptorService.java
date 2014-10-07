/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.api.registry;

import org.mule.api.MuleContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class MuleTransportDescriptorService implements TransportDescriptorService
{

    private final Map<String, TransportServiceDescriptorFactory> serviceDescriptorFactories = new HashMap<>();

    @Override
    public ServiceDescriptor getDescriptor(String name, MuleContext muleContext, Properties overrides) throws ServiceException
    {
        TransportServiceDescriptorFactory transportServiceDescriptorFactory = serviceDescriptorFactories.get(name);

        if (transportServiceDescriptorFactory == null)
        {
            throw new IllegalStateException(String.format("Unable to obtain a service descriptor for transport '%s'", name));
        }

        return transportServiceDescriptorFactory.create(muleContext, overrides);
    }

    @Override
    public void registerDescriptorFactory(String transport, TransportServiceDescriptorFactory factory)
    {
        //TODO(pablo.kraan): OSGi - synchronize access
        serviceDescriptorFactories.put(transport, factory);
    }

    @Override
    public boolean unregisterDescriptorFactory(String transport)
    {
        return serviceDescriptorFactories.remove(transport) != null;
    }
}
