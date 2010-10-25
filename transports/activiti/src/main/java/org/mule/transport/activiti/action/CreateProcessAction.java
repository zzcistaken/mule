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
import org.mule.transport.activiti.action.model.ProcessInstance;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class CreateProcessAction extends AbstractOutboundActivitiAction<ProcessInstance, PostMethod>
{

    @Override
    protected ProcessInstance processResult(ActivitiConnector connector, InputStream inputStream)
    {
        Map<String, Object> rootAsMap = connector.processJSON(inputStream);
        ProcessInstance instance = new ProcessInstance();
        connector.bindFields(instance, rootAsMap);
        return instance;
    }

    @Override
    protected URI resolveURI(OutboundEndpoint endpoint) throws URIException, NullPointerException
    {
        return new URI("process-instance", false);
    }

    public PostMethod getMethod()
    {
        return new PostMethod();
    }

    @Override
    protected void prepareMethod(PostMethod method, MuleMessage message) throws Exception
    {
        String value = message.getPayloadAsString();
        String json = "{"
            + "'processDefinitionId':'" + value + "'"
            + "}";
        
        RequestEntity requestEntity = new StringRequestEntity(json, null, null);
        method.setRequestEntity(requestEntity);
    }
}
