/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.mule.api.config.ConfigurationException;
import org.mule.api.registry.TransportServiceDescriptorFactory;
import org.mule.config.i18n.MessageFactory;
import org.mule.util.SpiUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.osgi.framework.Bundle;

/**
 *
 */
public class OsgiTransportServiceDescriptorFactoryFactory implements TransportServiceDescriptorFactoryFactory
{

    private final Bundle bundle;

    public OsgiTransportServiceDescriptorFactoryFactory(Bundle bundle)
    {
        this.bundle = bundle;
    }

    @Override
    public TransportServiceDescriptorFactory create(String transport) throws ConfigurationException
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
            throw new ConfigurationException(e);
        }

        return new DefaultTransportServiceDescriptorFactory(transport, props);
    }
}
