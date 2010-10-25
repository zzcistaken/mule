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

import org.mule.api.MuleException;
import org.mule.api.MuleRuntimeException;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.transport.AbstractConnector;
import org.mule.transport.http.HttpConnector;
import org.mule.util.BeanUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class ActivitiConnector extends AbstractConnector
{
    public static final String ACTIVITI = "activiti";

    private String activitiServerURL;

    private String username;

    private String password;

    private HttpConnectionManager clientConnectionManager;

    protected HttpClient getClient()
    {
        HttpClient client = new HttpClient();
        client.setState(new HttpState());
        client.setHttpConnectionManager(this.clientConnectionManager);

        return client;
    }

    public void prepareMethod(HttpMethod httpMethod, HttpClient client) throws UnsupportedEncodingException
    {
        httpMethod.setDoAuthentication(true);

        String authScopeHost = null;
        int authScopePort = -1;
        String authScopeRealm = null;
        String authScopeScheme = null;

        client.getState().setCredentials(
            new AuthScope(authScopeHost, authScopePort, authScopeRealm, authScopeScheme),
            new UsernamePasswordCredentials(this.getUsername(), this.getPassword()));
        client.getParams().setAuthenticationPreemptive(true);
    }
    
    public void bindFields(Object object, Map<String, Object> values)
    {
        BeanUtils.populateWithoutFail(object, values, false);
    }
    
    public Map<String, Object> processJSON(InputStream inputStream) {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            Map<String, Object> rootAsMap = mapper.readValue(inputStream, Map.class);
            return rootAsMap;
        }
        catch (JsonParseException e)
        {
            throw new MuleRuntimeException(null);
        }
        catch (JsonMappingException e)
        {
            throw new MuleRuntimeException(null);
        }
        catch (IOException e)
        {
            throw new MuleRuntimeException(null);
        }
    }
    
    /** 
     * {@inheritDoc} 
     */
    @Override
    protected void doConnect() throws Exception
    {
        // DO NOTHING
    }

    /** 
     * {@inheritDoc} 
     */
    @Override
    protected void doDisconnect() throws Exception
    {
        // DO NOTHING
    }

    /** 
     * {@inheritDoc} 
     */
    @Override
    protected void doDispose()
    {
        // DO NOTHING
    }

    /** 
     * {@inheritDoc} 
     */
    @Override
    protected void doInitialise() throws InitialisationException
    {
        this.clientConnectionManager = new MultiThreadedHttpConnectionManager();
    }

    /** 
     * {@inheritDoc} 
     */
    @Override
    protected void doStart() throws MuleException
    {
        // DO NOTHING
    }

    /** 
     * {@inheritDoc} 
     */
    @Override
    protected void doStop() throws MuleException
    {
        // DO NOTHING
    }

    /** 
     * {@inheritDoc} 
     */
    public String getProtocol()
    {
        return ACTIVITI;
    }

    public String getActivitiServerURL()
    {
        return activitiServerURL;
    }

    public void setActivitiServerURL(String activitiServerURL)
    {
        this.activitiServerURL = activitiServerURL;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
