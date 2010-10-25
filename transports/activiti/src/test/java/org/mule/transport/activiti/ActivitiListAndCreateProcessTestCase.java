/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.activiti;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

public class ActivitiListAndCreateProcessTestCase extends FunctionalTestCase
{

    public void testGetProcessDefinitions() throws Exception
    {
        MuleClient client = new MuleClient();
        MuleMessage message = client.request("vm://out", 10000);
        String processDefinitionId = message.getPayloadAsString();
        assertEquals("financialReport:1", processDefinitionId);
    }

    @Override
    protected String getConfigResources()
    {
        return "activiti-processes-config.xml";
    }
}
