/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.soap.axis;

import org.mule.api.MuleMessage;
import org.mule.endpoint.DefaultOutboundEndpoint;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

import java.util.Collection;
import java.util.Properties;

public class AxisMemoryLeakTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "axis-memory-leak-config.xml";
    }

    /**
     * Relates to MULE-5353
     */
    public void testAxisOutboundDoesNotCreatesNewEndpoints() throws Exception
    {
        MuleClient client = new MuleClient();
        Properties props = new Properties();
        props.setProperty("ws.service.url", "http://localhost:65081/services/TestUMO?method=receive");
        client.send("vm://testaddress?connector=VMConnector", TEST_MESSAGE, props);
        int originalSize = getNumberOfOutboundEndpoints();

        MuleMessage result = client.send("vm://testaddress?connector=VMConnector", TEST_MESSAGE, props);
        assertEquals("Payload", "Received: " + TEST_MESSAGE, result.getPayloadAsString());
        assertEquals(originalSize, getNumberOfOutboundEndpoints());
    }

    private int getNumberOfOutboundEndpoints()
    {
        Collection originalEndpoints = muleContext.getRegistry().lookupObjects(DefaultOutboundEndpoint.class);
        return originalEndpoints.size();
    }
}
