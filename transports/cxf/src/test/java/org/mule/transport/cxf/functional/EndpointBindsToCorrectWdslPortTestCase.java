/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.cxf.functional;

import org.mule.api.routing.InboundRouterCollection;
import org.mule.api.service.Service;
import org.mule.endpoint.DefaultInboundEndpoint;
import org.mule.tck.FunctionalTestCase;
import org.mule.transport.cxf.CxfConnector;
import org.mule.transport.cxf.CxfMessageReceiver;
import org.mule.transport.cxf.transport.MuleUniversalDestination;

import java.util.List;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.service.model.EndpointInfo;

public class EndpointBindsToCorrectWdslPortTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "org/mule/transport/cxf/functional/endpoint-binds-to-correct-wdsl-port.xml";
    }

    public void testThatTheCorrectSoapPortIsChosen() throws Exception
    {
        final Service service = muleContext.getRegistry().lookupService("CXFProxyService");

        final InboundRouterCollection inboundRouter = service.getInboundRouter();
        final List<?> endpoints = inboundRouter.getEndpoints();
        final DefaultInboundEndpoint inboundEndpoing = (DefaultInboundEndpoint) endpoints.get(0);
        final CxfConnector connector = (CxfConnector) inboundEndpoing.getConnector();
        final CxfMessageReceiver receiver = (CxfMessageReceiver) connector.getReceiver(service,
            inboundEndpoing);
        final Server server = receiver.getServer();
        final MuleUniversalDestination destination = (MuleUniversalDestination) server.getDestination();
        final EndpointInfo endpointInfo = destination.getEndpointInfo();

        assertEquals(
            "The local part of the endpoing name must be the one supplied as the endpointName parameter on the cxf:inbound-endpoint",
            "ListsSoap", endpointInfo.getName().getLocalPart());
    }
}
