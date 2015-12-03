/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.firstextension;

import org.mule.api.connection.ConnectionException;
import org.mule.api.connection.ConnectionHandlingStrategy;
import org.mule.api.connection.ConnectionHandlingStrategyFactory;
import org.mule.api.connection.ConnectionProvider;
import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.param.Optional;

/**
 * Created by pablocabrera on 11/26/15.
 */
public class FirstExtensionConnectionProvider implements ConnectionProvider<FirstExtension,FirstConnection>
{
    @Parameter
    @Optional(defaultValue = "5555")
    private String providerConfigurable;

    @Override
    public FirstConnection connect(FirstExtension firstExtension) throws ConnectionException
    {
        return null;
    }

    @Override
    public void disconnect(FirstConnection firstConnection)
    {

    }

    @Override
    public ConnectionHandlingStrategy<FirstConnection> getHandlingStrategy(ConnectionHandlingStrategyFactory connectionHandlingStrategyFactory)
    {
        return null;
    }

    public String getProviderConfigurable()
    {
        return providerConfigurable;
    }

    public void setProviderConfigurable(String providerConfigurable)
    {
        this.providerConfigurable = providerConfigurable;
    }
}
