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

import org.mule.api.transport.Connector;
import org.mule.transport.AbstractConnectorTestCase;
import org.mule.transport.activiti.action.model.ProcessDefinition;

import java.util.ArrayList;
import java.util.List;

public class ActivitiConnectorTestCase extends AbstractConnectorTestCase
{

    public Connector createConnector() throws Exception
    {
        ActivitiConnector c = new ActivitiConnector();
        c.setName("Test");
        c.setActivitiServerURL("http://localhost:8080/activiti-rest/service/");
        return c;
    }

    public String getTestEndpointURI()
    {
        return "activiti://localhost:8080/activiti-rest/service/";
    }

    public Object getValidMessage() throws Exception
    {
        List<ProcessDefinition> definitions = new ArrayList<ProcessDefinition>();
        return definitions;
    }


    public void testProperties() throws Exception
    {
        ActivitiConnector c = (ActivitiConnector) this.getConnector();
        assertEquals("http://localhost:8080/activiti-rest/service/", c.getActivitiServerURL());
    }
}