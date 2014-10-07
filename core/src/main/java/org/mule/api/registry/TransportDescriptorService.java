/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.api.registry;

import org.mule.api.MuleContext;

import java.util.Properties;

/**
 *
 */
public interface TransportDescriptorService
{

    ServiceDescriptor getDescriptor(String name, MuleContext muleContext, Properties overrides) throws ServiceException;

    void registerDescriptorFactory(String transport, TransportServiceDescriptorFactory factory);

    boolean unregisterDescriptorFactory(String transport);

}
