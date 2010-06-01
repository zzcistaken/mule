/*
 * $Id: ObjectFactoryTestCase.java 10489 2008-01-23 17:53:38Z dfeist $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

/**
 * Test for MULE-4412 : selective-consumer filter is applied twice
 */
public class Mule4412TestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "mule-4412.xml";
    }

    public void testPositive() throws Exception
    {
        DefaultMuleMessage msg = new DefaultMuleMessage(TEST_MESSAGE);
        msg.setProperty("messageType", "async");
        MuleClient client = new MuleClient();
        client.send("vm://async", msg);
        MuleMessage reply = client.request("vm://asyncResponse", 5000);
        assertNotNull(reply);
        assertEquals("wrong message received : " + reply.getPayloadAsString(), TEST_MESSAGE+"a",
            reply.getPayloadAsString());
        assertEquals("messageType not correct", "async", reply.getProperty("messageType"));
    }
    
    public void testWrongProperty() throws Exception
    {
        DefaultMuleMessage msg = new DefaultMuleMessage(TEST_MESSAGE);
        msg.setProperty("messageType", "foo");
        MuleClient client = new MuleClient();
        client.send("vm://async", msg);
        MuleMessage reply = client.request("vm://asyncResponse", 5000);
        assertNull(reply);
    }
    
    public void testNoProperty() throws Exception
    {
        DefaultMuleMessage msg = new DefaultMuleMessage(TEST_MESSAGE);
        MuleClient client = new MuleClient();
        client.send("vm://async", msg);
        MuleMessage reply = client.request("vm://asyncResponse", 5000);
        assertNull(reply);
    }
}
