/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.jbpm;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.transport.bpm.BPMS;
import org.mule.transport.bpm.ProcessConnector;

/**
 * Tests the connector against jBPM with a process which generates 
 * a Mule message and processes its response. jBPM is instantiated by Spring. 
 * 
 * @deprecated It is recommended to configure BPM as a component rather than a transport for 3.x
 */
public class MessagingJbpmTestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "jbpm-functional-test.xml";
    }

    public void testSendMessageProcess() throws Exception
    {
        ProcessConnector connector = (ProcessConnector) muleContext.getRegistry().lookupConnector("bpmConnector");
        BPMS bpms = connector.getBpms();
        assertNotNull(bpms);

        MuleClient client = new MuleClient(muleContext);
        try
        {
            // Create a new process.
            MuleMessage response = client.send("bpm://message", "data", null);
            Object process = response.getPayload();
            assertTrue(bpms.isProcess(process)); 

            String processId = (String)bpms.getId(process);
            // The process should have sent a synchronous message, followed by an asynchronous message and now be in a wait state.
            assertFalse(processId == null);
            assertEquals("waitForResponse", bpms.getState(process));

            // Advance the process one step.
            response = client.send("bpm://message/" + processId, "data", null);
            process = response.getPayload();

            // The process should have ended.
            assertTrue(bpms.hasEnded(process));
        }
        finally
        {
            client.dispose();
        }
    }
}
