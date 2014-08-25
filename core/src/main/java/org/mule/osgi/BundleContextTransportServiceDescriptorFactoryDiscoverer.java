/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.mule.api.DefaultMuleException;
import org.mule.api.MuleException;
import org.mule.api.config.ConfigurationException;
import org.mule.api.registry.TransportServiceDescriptorFactory;
import org.mule.config.builders.ConfigurationBuilderFactory;
import org.mule.config.i18n.MessageFactory;
import org.mule.transport.service.TransportServiceDescriptor;
import org.mule.util.SpiUtils;
import org.mule.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 */
public class BundleContextTransportServiceDescriptorFactoryDiscoverer implements TransportServiceDescriptorFactoryDiscoverer
{

    private final BundleContext bundleContext;

    public BundleContextTransportServiceDescriptorFactoryDiscoverer(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

    @Override
    public List<ServiceRegistration> discover() throws MuleException
    {
        List<ServiceRegistration> descriptorRefs = new ArrayList<>();
        final Bundle bundle = bundleContext.getBundle();
        Dictionary headers = bundle.getHeaders();

        // Transports are defined in the bundle manifest using Mule-Transports header
        String transportHeader = (String) headers.get(TransportServiceDescriptor.OSGI_HEADER_TRANSPORT);
        if (transportHeader == null)
        {
            throw new ConfigurationException(MessageFactory.createStaticMessage("Transport must declare its protocol(s) as an OSGi header."));
        }

        String[] transports = StringUtils.splitAndTrim(transportHeader, ",");

        for (final String transport : transports)
        {
            // Look up the service descriptor file (e.g., "tcp.serviceProperties")
            String descriptorPath = "/" + SpiUtils.SERVICE_ROOT + SpiUtils.PROVIDER_SERVICE_PATH + transport + ".properties";
            URL descriptorUrl = bundle.getEntry(descriptorPath);
            if (descriptorUrl == null)
            {
                throw new ConfigurationException(MessageFactory.createStaticMessage("Unable to locate service descriptor file: " + descriptorPath));
            }

            final Properties props = new Properties();
            try
            {
                props.load(descriptorUrl.openStream());
            }
            catch (IOException e)
            {
                throw new DefaultMuleException(e);
            }

            TransportServiceDescriptorFactory serviceDescriptorFactory = new DefaultTransportServiceDescriptorFactory(transport, props);

            Dictionary<String, String> serviceProperties = new Hashtable<>();
            serviceProperties.put(ConfigurationBuilderFactory.EXTENSION, "xml");
            serviceProperties.put("transport", transport);

            descriptorRefs.add(bundleContext.registerService(TransportServiceDescriptorFactory.class.getName(), serviceDescriptorFactory, serviceProperties));
        }

        return descriptorRefs;
    }
}

