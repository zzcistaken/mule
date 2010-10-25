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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

public interface InboundActivitiAction<V extends HttpMethod>
{
    V getMethod();

    Object executeUsing(ActivitiConnector connector, HttpClient client, InboundEndpoint endpoint);
}


