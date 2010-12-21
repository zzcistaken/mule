/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.security;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

public class CustomSecurityFilterTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/security/custom-security-filter.xml";
    }

    public void testCustomAutenticationFail() throws Exception
    {
        MuleClient mc = new MuleClient();
        mc.dispatch("vm://in", TEST_MESSAGE + "a", null);
        MuleMessage message = mc.request("vm://out", RECEIVE_TIMEOUT);
        assertNull(message);
    }
    
    public void testCustomAutenticationAccept() throws Exception
    {
        MuleClient mc = new MuleClient();
        mc.dispatch("vm://in", TEST_MESSAGE, null);
        MuleMessage message = mc.request("vm://out", RECEIVE_TIMEOUT);
        assertNotNull(message);
        assertEquals(TEST_MESSAGE, message.getPayloadAsString());
    }
}
