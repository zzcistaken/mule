/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.mule.api.MuleContext;
import org.mule.api.registry.ServiceDescriptor;
import org.mule.api.registry.ServiceDescriptorFactory;
import org.mule.api.registry.ServiceException;
import org.mule.api.registry.ServiceType;
import org.mule.api.registry.TransportServiceDescriptorFactory;

import java.util.Properties;

public class DefaultTransportServiceDescriptorFactory implements TransportServiceDescriptorFactory
{

    private final String transport;
    private final Properties props;
    private ServiceDescriptor serviceDescriptor;

    public DefaultTransportServiceDescriptorFactory(String transport, Properties props)
    {
        this.transport = transport;
        this.props = new Properties();
        this.props.putAll(props);
    }

    @Override
    public ServiceDescriptor create(MuleContext muleContext, Properties overrides) throws ServiceException
    {
        Properties properties = new Properties();
        properties.putAll(props);
        if (overrides != null)
        {
            properties.putAll(overrides);
        }

        serviceDescriptor = ServiceDescriptorFactory.create(ServiceType.TRANSPORT, transport, properties, null, muleContext, muleContext.getExecutionClassLoader());

        return serviceDescriptor;
    }
}
