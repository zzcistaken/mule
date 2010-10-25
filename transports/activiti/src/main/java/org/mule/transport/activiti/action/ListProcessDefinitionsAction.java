/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.activiti.action;

import org.mule.api.endpoint.InboundEndpoint;
import org.mule.transport.activiti.ActivitiConnector;
import org.mule.transport.activiti.action.model.ProcessDefinition;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;

public class ListProcessDefinitionsAction extends AbstractInboundActivitiAction<List<ProcessDefinition>, GetMethod>
{

    @Override
    protected List<ProcessDefinition> processResult(ActivitiConnector connector, InputStream inputStream)
    {
        List<ProcessDefinition> processDefinitions = new ArrayList<ProcessDefinition>();

        Map<String, Object> rootAsMap = connector.processJSON(inputStream);
        List processDefinitionsAsMaps = (List) rootAsMap.get("data");
        
        for (Object o : processDefinitionsAsMaps) {
            ProcessDefinition definition = new ProcessDefinition();
            Map<String, Object> map = (Map<String, Object>) o;

            connector.bindFields(definition, map);
            processDefinitions.add(definition);
        }
        
        return processDefinitions;
    }
    
    @Override
    protected URI resolveURI(InboundEndpoint endpoint) throws URIException, NullPointerException
    {
        return new URI("process-definitions", false);
    }

    public GetMethod getMethod()
    {
        return new GetMethod();
    }

    @Override
    protected void prepareMethod(GetMethod method)
    {
        //DO NOTHING
    }
}