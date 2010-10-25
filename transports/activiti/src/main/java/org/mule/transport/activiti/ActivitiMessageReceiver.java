/*
 * $Id: MessageReceiver.vm 11079 2008-02-27 15:52:01Z tcarlson $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.activiti;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.CreateException;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.service.Service;
import org.mule.api.transport.Connector;
import org.mule.transport.AbstractPollingMessageReceiver;
import org.mule.transport.activiti.action.InboundActivitiAction;

import org.apache.commons.httpclient.HttpClient;

public class ActivitiMessageReceiver extends AbstractPollingMessageReceiver
{
    private InboundActivitiAction<?> action;

    private HttpClient client;

    public ActivitiMessageReceiver(Connector connector, Service service, InboundEndpoint endpoint)
        throws CreateException
    {
        super(connector, service, endpoint);
    }

    /** 
     * {@inheritDoc} 
     */
    @Override
    protected void doConnect() throws Exception
    {
        super.doConnect();
        if (this.client == null)
        {
            this.client = this.getConnector().getClient();
        }
    }

    /** 
     * {@inheritDoc} 
     */
    @Override
    protected void doDisconnect() throws Exception
    {
        super.doDisconnect();
        this.client = null;
    }
    
    /** 
     * {@inheritDoc} 
     */
    @Override
    protected void doInitialise() throws InitialisationException
    {
        super.doInitialise();
        this.action = (InboundActivitiAction<?>) this.getEndpoint().getProperty("action");
    }

    /** 
     * {@inheritDoc} 
     */
    @Override
    public void poll() throws Exception
    {
        Object result = this.action.executeUsing(this.getConnector(), this.client, this.getEndpoint());
        MuleMessage message = new DefaultMuleMessage(result);
        this.routeMessage(message);
    }

    @Override
    public ActivitiConnector getConnector()
    {
        return (ActivitiConnector) super.getConnector();
    }
}