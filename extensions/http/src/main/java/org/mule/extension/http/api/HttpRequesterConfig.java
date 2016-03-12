/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.http.api;

import org.mule.extension.api.annotation.Configuration;
import org.mule.extension.api.annotation.Parameter;
import org.mule.extension.api.annotation.param.Optional;
import org.mule.module.http.internal.request.grizzly.GrizzlyHttpClientConfiguration;
import org.mule.module.socket.internal.DefaultTcpClientSocketProperties;

@Configuration(name = "requester-config")
public class HttpRequesterConfig extends HttpConfig
{
    //this first 3 are candidates for a parameter group
    @Parameter
    private String protocol = "http";

    @Parameter
    private String host;

    @Parameter
    private String port;

    @Parameter
    @Optional(defaultValue = "/")
    private String basePath;

    @Parameter
    @Optional(defaultValue = "true")
    private String followRedirects;

    @Parameter
    @Optional(defaultValue = "AUTO")
    private String requestStreamingMode;

    @Parameter
    @Optional(defaultValue = "AUTO")
    private String sendBodyMode;

    @Parameter
    @Optional(defaultValue = "true")
    private String parseResponse;

    @Parameter
    @Optional
    private String responseTimeout;


    private GrizzlyHttpClientConfiguration grizzlyConfig;

    public GrizzlyHttpClientConfiguration getGrizzlyConfig()
    {
        //here we would set up all parameters for the grizzly client
        GrizzlyHttpClientConfiguration.Builder grizzlyConfigBuilder = new GrizzlyHttpClientConfiguration.Builder();
        return grizzlyConfigBuilder.setClientSocketProperties(new DefaultTcpClientSocketProperties()).setMaxConnections(-1).setUsePersistentConnections(true).setConnectionIdleTimeout(30 * 1000).setOwnerName("deadpool").setThreadNamePrefix("%shttp.requester.%s").build();
    }

    public String getProtocol()
    {
        return protocol;
    }

    public String getHost()
    {
        return host;
    }

    public String getPort()
    {
        return port;
    }
}
