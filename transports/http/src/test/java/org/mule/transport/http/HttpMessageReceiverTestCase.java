/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.http;

import org.mule.api.endpoint.EndpointBuilder;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.service.Service;
import org.mule.api.transport.MessageReceiver;
import org.mule.endpoint.EndpointURIEndpointBuilder;
import org.mule.transport.AbstractMessageReceiverTestCase;
import org.mule.transport.http.transformers.MuleMessageToHttpResponse;
import org.mule.util.CollectionUtils;

import com.mockobjects.dynamic.Mock;

import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpVersion;

public class HttpMessageReceiverTestCase extends AbstractMessageReceiverTestCase
{
    public MessageReceiver getMessageReceiver() throws Exception
    {
        Mock mockComponent = new Mock(Service.class);
        mockComponent.expectAndReturn("getResponseTransformer", null);
        mockComponent.expectAndReturn("getResponseRouter", null);

        return new HttpMessageReceiver(endpoint.getConnector(), (Service) mockComponent.proxy(), endpoint);
    }

    public InboundEndpoint getEndpoint() throws Exception
    {
        EndpointBuilder endpointBuilder = new EndpointURIEndpointBuilder("http://localhost:6789", muleContext);
        endpointBuilder.setResponseTransformers(CollectionUtils.singletonList(new MuleMessageToHttpResponse()));
        endpoint = muleContext.getRegistry().lookupEndpointFactory().getInboundEndpoint(endpointBuilder);
        return endpoint;
    }
    
    public void testMultipleHeaderWithSameName() throws Exception
    {
        Mock mockComponent = new Mock(Service.class);
        mockComponent.expectAndReturn("getResponseTransformer", null);
        mockComponent.expectAndReturn("getResponseRouter", null);

        HttpMessageReceiver receiver = new HttpMessageReceiver(endpoint.getConnector(),
            (Service) mockComponent.proxy(), endpoint);

        RequestLine requestLine = new RequestLine("GET", "http://localhost", HttpVersion.HTTP_1_1);
        Header[] headers = new Header[4];
        headers[0] = new Header("k2", "priority");
        headers[1] = new Header("k1", "top");
        headers[2] = new Header("k2", "always");
        headers[3] = new Header("k2", "true");
        HttpRequest request = new HttpRequest(requestLine, headers);

        Map parsedHeaders = receiver.parseHeaders(request, true, "");

        assertEquals(6, parsedHeaders.size());
        assertEquals("top", parsedHeaders.get("k1"));
        assertEquals("priority,always,true", parsedHeaders.get("k2"));
    }
}
