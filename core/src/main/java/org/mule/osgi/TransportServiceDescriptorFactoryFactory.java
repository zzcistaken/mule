/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.mule.api.config.ConfigurationException;
import org.mule.api.registry.TransportServiceDescriptorFactory;

/**
 *
 */
public interface TransportServiceDescriptorFactoryFactory
{

    TransportServiceDescriptorFactory create(String transport) throws ConfigurationException;

}
