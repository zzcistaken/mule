/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.multiconfig;

import org.mule.api.connection.ConnectionException;
import org.mule.api.connection.ConnectionHandlingStrategy;
import org.mule.api.connection.ConnectionHandlingStrategyFactory;
import org.mule.api.connection.ConnectionProvider;
import org.mule.extension.annotation.api.Parameter;

/**
 * Created by pablocabrera on 11/26/15.
 */
public class MultiConfigProvider implements ConnectionProvider<AbstractConfig, MultiConfigConnection>
{

    @Parameter
    private String hashCode;

    @Override
    public MultiConfigConnection connect(AbstractConfig abstractConfig) throws ConnectionException
    {
        return null;
    }

    @Override
    public void disconnect(MultiConfigConnection multiConfigConnection)
    {

    }

    @Override
    public ConnectionHandlingStrategy<MultiConfigConnection> getHandlingStrategy(ConnectionHandlingStrategyFactory connectionHandlingStrategyFactory)
    {
        return null;
    }

    public String getHashCode()
    {
        return hashCode;
    }

    public void setHashCode(String hashCode)
    {
        this.hashCode = hashCode;
    }
}
