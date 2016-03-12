/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.http.api;

import org.mule.api.connection.ConnectionException;
import org.mule.api.connection.ConnectionHandlingStrategy;
import org.mule.api.connection.ConnectionHandlingStrategyFactory;
import org.mule.api.connection.ConnectionProvider;
import org.mule.api.connection.ConnectionValidationResult;
import org.mule.module.http.internal.request.grizzly.GrizzlyHttpClient;

public class HttpRequesterProvider implements ConnectionProvider<HttpConfig, GrizzlyHttpClient>
{

    public GrizzlyHttpClient connect(HttpConfig httpConnector) throws ConnectionException
    {
        //here we'll set up the grizzly client that will be injected to the operations
        GrizzlyHttpClient grizzlyHttpClient = new GrizzlyHttpClient(((HttpRequesterConfig)httpConnector).getGrizzlyConfig());
        try
        {
            grizzlyHttpClient.initialise();
        }
        catch (org.mule.api.lifecycle.InitialisationException e)
        {
            e.printStackTrace();
        }
        return grizzlyHttpClient;
    }

    public void disconnect(GrizzlyHttpClient grizzlyHttpClient)
    {
        grizzlyHttpClient.stop();
    }

    public ConnectionValidationResult validate(GrizzlyHttpClient grizzlyHttpClient)
    {
        return null;
    }

    public ConnectionHandlingStrategy<GrizzlyHttpClient> getHandlingStrategy(ConnectionHandlingStrategyFactory<HttpConfig, GrizzlyHttpClient> connectionHandlingStrategyFactory)
    {
        return connectionHandlingStrategyFactory.cached();
    }
}
