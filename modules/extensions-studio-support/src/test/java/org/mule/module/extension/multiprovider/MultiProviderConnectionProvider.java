/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.multiprovider;

import org.mule.api.connection.ConnectionException;
import org.mule.api.connection.ConnectionHandlingStrategy;
import org.mule.api.connection.ConnectionHandlingStrategyFactory;
import org.mule.api.connection.ConnectionProvider;
import org.mule.extension.annotation.api.Parameter;
import org.mule.module.extension.multiconfig.AbstractConfig;

/**
 * Created by pablocabrera on 11/26/15.
 */
public class MultiProviderConnectionProvider implements ConnectionProvider<AbstractConfig, MultiProviderConnection>
{

    @Parameter
    private String anotherUsername;

    @Parameter
    private String password;

    @Override
    public MultiProviderConnection connect(AbstractConfig abstractConfig) throws ConnectionException
    {
        return null;
    }

    @Override
    public void disconnect(MultiProviderConnection multiConfigConnection)
    {

    }

    @Override
    public ConnectionHandlingStrategy<MultiProviderConnection> getHandlingStrategy(ConnectionHandlingStrategyFactory connectionHandlingStrategyFactory)
    {
        return null;
    }
}
