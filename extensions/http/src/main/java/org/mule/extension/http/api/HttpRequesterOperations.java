/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.http.api;

import org.mule.DefaultMuleMessage;
import org.mule.api.temporary.MuleMessage;
import org.mule.extension.api.annotation.param.Connection;
import org.mule.extension.api.annotation.param.Optional;
import org.mule.extension.api.annotation.param.UseConfig;
import org.mule.module.http.internal.ParameterMap;
import org.mule.module.http.internal.domain.EmptyHttpEntity;
import org.mule.module.http.internal.domain.InputStreamHttpEntity;
import org.mule.module.http.internal.domain.request.DefaultHttpRequest;
import org.mule.module.http.internal.domain.request.HttpRequest;
import org.mule.module.http.internal.domain.response.HttpResponse;
import org.mule.module.http.internal.request.grizzly.GrizzlyHttpClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class HttpRequesterOperations
{

    public MuleMessage<?, ?> request(@Connection GrizzlyHttpClient httpClient, @UseConfig HttpConfig config, MuleMessage<?, ?> message, String path, @Optional(defaultValue = "GET") String method)
    {
        HttpRequesterConfig httpConfig = (HttpRequesterConfig) config;
        HttpRequest request = new DefaultHttpRequest(String.format("%s://%s:%s", httpConfig.getProtocol(), httpConfig.getHost(), httpConfig.getPort()), path, method, new ParameterMap(), new ParameterMap(), new EmptyHttpEntity());
        HttpResponse response = null;
        try
        {
            response = httpClient.send(request, 5000, true, null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            e.printStackTrace();
        }
        return new DefaultMuleMessage(((InputStreamHttpEntity) response.getEntity()).getInputStream());
    }

}
