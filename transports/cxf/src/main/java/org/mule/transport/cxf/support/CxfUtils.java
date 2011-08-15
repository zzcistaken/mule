/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.cxf.support;

import org.mule.api.endpoint.EndpointNotFoundException;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.cxf.binding.soap.SoapBindingConstants;
import org.apache.cxf.binding.soap.SoapVersion;
import org.apache.cxf.binding.soap.SoapVersionFactory;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.ChainInitiationObserver;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.MessageObserver;

public final class CxfUtils
{

    @SuppressWarnings("unchecked")
    public static void removeInterceptor(List<Interceptor> inInterceptors, String name) {

        for (Interceptor<?> i : inInterceptors) {
            if (i instanceof PhaseInterceptor) {
                PhaseInterceptor<Message> p = (PhaseInterceptor<Message>)i;

                if (p.getId().equals(name)) {
                    inInterceptors.remove(p);
                    return;
                }
            }
        }
    }

    public static Endpoint getEndpoint(DestinationFactory df, String uri)
        throws IOException, EndpointNotFoundException
    {
        int idx = uri.indexOf('?');
        if (idx != -1)
        {
            uri = uri.substring(0, idx);
        }

        EndpointInfo ei = new EndpointInfo();
        ei.setAddress(uri);

        Destination d = df.getDestination(ei);
        if (d.getMessageObserver() == null)
        {
            // TODO is this the right Mule exception?
            throw new EndpointNotFoundException(uri);
        }

        MessageObserver mo = d.getMessageObserver();
        if (!(mo instanceof ChainInitiationObserver))
        {
            throw new EndpointNotFoundException(uri);
        }

        ChainInitiationObserver co = (ChainInitiationObserver) mo;
        return co.getEndpoint();
    }

    public static String getBindingIdForSoapVersion(String version)
    {
        if("1.1".equals(version))
        {
            return SoapBindingConstants.SOAP11_BINDING_ID;
        }
        else if("1.2".equals(version))
        {
            return SoapBindingConstants.SOAP12_BINDING_ID;
        }
        throw new IllegalArgumentException("Invalid soap version " + version);
    }

}
