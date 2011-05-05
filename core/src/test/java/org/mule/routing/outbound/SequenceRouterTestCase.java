/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.routing.outbound;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.MuleMessageCollection;
import org.mule.api.MuleSession;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.tck.AbstractMuleTestCase;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;

public class SequenceRouterTestCase extends AbstractMuleTestCase
{

    private MuleSession session;
    private ImmutableEndpoint endpoint1;
    private ImmutableEndpoint endpoint2;
    protected SequenceRouter router;

    @Override
    protected void doSetUp() throws Exception
    {
        session = Mockito.mock(MuleSession.class);
        Mockito.when(session.getService()).thenReturn(getTestService());

        endpoint1 = getTestOutboundEndpoint("Test1Provider", "test://Test1Provider?synchronous=true");
        assertNotNull(endpoint1);

        endpoint2 = getTestOutboundEndpoint("Test2Provider", "test://Test2Provider?synchronous=true");
        assertNotNull(endpoint2);

        router = new SequenceRouter();
        List endpoints = new ArrayList();
        endpoints.add(endpoint1);
        endpoints.add(endpoint2);
        router.setEndpoints(endpoints);
    }

    public void testSyncEndpointsOk() throws Exception
    {
        MuleMessage message = new DefaultMuleMessage(TEST_MESSAGE);

        Mockito.when(session.sendEvent(Mockito.<MuleMessage>any(MuleMessage.class), Mockito.same((OutboundEndpoint) endpoint1))).thenReturn(message);
        Mockito.when(session.sendEvent(Mockito.<MuleMessage>any(MuleMessage.class), Mockito.same((OutboundEndpoint) endpoint2))).thenReturn(message);

        MuleMessage result = router.route(message, session);
        assertNotNull(result);
        assertTrue(result instanceof MuleMessageCollection);
        assertEquals(2, ((MuleMessageCollection) result).size());
    }

    public void testSyncEndpointsWithFirstOneFailing() throws Exception
    {
        MuleMessage message = new DefaultMuleMessage(TEST_MESSAGE);

        Mockito.when(session.sendEvent(Mockito.<MuleMessage>any(MuleMessage.class), Mockito.same((OutboundEndpoint) endpoint1))).thenReturn(null);
        MuleMessage result = router.route(message, session);
        assertNull(result);
    }

    public void testSyncEndpointsWithLastOneFailing() throws Exception
    {
        MuleMessage message = new DefaultMuleMessage(TEST_MESSAGE);

        Mockito.when(session.sendEvent(Mockito.<MuleMessage>any(MuleMessage.class), Mockito.same((OutboundEndpoint) endpoint1))).thenReturn(message);
        Mockito.when(session.sendEvent(Mockito.<MuleMessage>any(MuleMessage.class), Mockito.same((OutboundEndpoint) endpoint2))).thenReturn(null);

        MuleMessage result = router.route(message, session);
        assertNotNull(result);
        assertTrue(result instanceof MuleMessageCollection);
        assertEquals(1, ((MuleMessageCollection) result).size());
    }
}
