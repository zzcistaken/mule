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
import org.mule.transport.activiti.action.Operation;

import java.util.Map;

public class ActivitiListAndPerformOperationTaskTestCase extends FunctionalTestCase
{

    public void testGetTasks() throws Exception
    {
        MuleClient client = new MuleClient();
        MuleMessage message = client.request("vm://out", 10000);
        Map selectedTask = (Map) message.getPayload();
        assertFalse(selectedTask.isEmpty());
        assertEquals("116", selectedTask.get("taskId"));
        assertEquals(Operation.CLAIM, selectedTask.get("operation"));
    }

    @Override
    protected String getConfigResources()
    {
        return "activiti-tasks-config.xml";
    }
}
