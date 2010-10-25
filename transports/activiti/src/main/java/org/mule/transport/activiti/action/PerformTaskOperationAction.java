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

import org.mule.api.MuleMessage;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.transport.activiti.ActivitiConnector;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class PerformTaskOperationAction extends AbstractOutboundActivitiAction<Boolean, PutMethod>
{
    private String taskId;
    
    private Operation operation;
    
    @Override
    protected void prepareMethod(PutMethod method, MuleMessage message) throws Exception
    {
        RequestEntity requestEntity = new StringRequestEntity("{}", null, null);
        method.setRequestEntity(requestEntity);
        
        if (message.getPayload() instanceof Map) {
            Map values = (Map) message.getPayload();
            
            if (values.containsKey("taskId")) {
                this.setTaskId((String) values.get("taskId"));
            }
            if (values.containsKey("operation")) {
                this.setOperation((Operation) values.get("operation"));
            }
        }
    }

    @Override
    protected Boolean processResult(ActivitiConnector connector, InputStream inputStream)
    {
        Map<String, Object> rootAsMap = connector.processJSON(inputStream);
        return (Boolean) rootAsMap.get("success");
    }

    @Override
    protected URI resolveURI(OutboundEndpoint endpoint) throws URIException, NullPointerException
    {
        StringBuffer uri = new StringBuffer();
        uri.append("task/");

        uri.append(this.getTaskId());
        uri.append("/");
        uri.append(this.getOperation());

        return new URI(uri.toString(), false);
    }

    public PutMethod getMethod()
    {
        return new PutMethod();
    }

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public Operation getOperation()
    {
        return operation;
    }

    public void setOperation(Operation operation)
    {
        this.operation = operation;
    }
}
