/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.MuleSession;
import org.mule.api.routing.OutboundRouter;
import org.mule.tck.AbstractMuleTestCase;

import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

/**
 * @author estebanroblesluna
 * @since 2.2.6
 */
public class RouteableExceptionStrategyTestCase extends AbstractMuleTestCase
{
    private class MuleMessageMatcher extends ArgumentMatcher<MuleMessage>
    {
        private Object expectedPayload;

        public MuleMessageMatcher(Object expectedPayload)
        {
            this.expectedPayload = expectedPayload;
        }

        @Override
        public boolean matches(Object argument)
        {
            return ((MuleMessage) argument).getPayload().equals(this.expectedPayload);
        }
    }

    private RouteableExceptionStrategy strategy;

    private OutboundRouter router;

    private DefaultMuleMessage message;

    private DefaultMuleEvent event;

    private MuleEvent oldEvent;
    
    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        this.oldEvent = RequestContext.getEvent();

        this.router = Mockito.mock(OutboundRouter.class);
        this.strategy = new RouteableExceptionStrategy();
        this.strategy.setRouter(this.router);
        this.strategy.setMuleContext(muleContext);

        this.message = new DefaultMuleMessage(1);

        this.event = new DefaultMuleEvent(message, null, new DefaultMuleSession(muleContext), true);

        this.event.setTimeout(10000);

        RequestContext.setEvent(this.event);
    }

    @Override
    protected void doTearDown() throws Exception
    {
        super.doTearDown();
        RequestContext.setEvent(this.oldEvent);
    }


    public void testRouteNoTransformers() throws Exception
    {
        MuleMessage expectedMuleMessage = null;
        Mockito.when(
            this.router.route(Mockito.argThat(new MuleMessageMatcher(1)), (MuleSession) Mockito.any()))
            .thenReturn(expectedMuleMessage);

        Exception e = new IllegalArgumentException("boom");

        this.strategy.exceptionThrown(e);

        Mockito.verify(this.router).route(Mockito.argThat(new MuleMessageMatcher(1)),
            (MuleSession) Mockito.any());
        assertEquals(1, this.message.getPayload());
    }

    public void testRouteRequestTransformers() throws Exception
    {
        MuleMessage expectedMuleMessage = null;
        Mockito.when(
            this.router.route(Mockito.argThat(new MuleMessageMatcher(1)), (MuleSession) Mockito.any()))
            .thenReturn(expectedMuleMessage);

        Exception e = new IllegalArgumentException("boom");

        this.strategy.exceptionThrown(e);

        Mockito.verify(this.router).route(Mockito.argThat(new MuleMessageMatcher(1)),
            (MuleSession) Mockito.any());
        assertEquals(1, this.message.getPayload());
    }

    public void testRouteResponseTransformers() throws Exception
    {
        MuleMessage expectedMuleMessage = this.message;
        Mockito.when(
            this.router.route(Mockito.argThat(new MuleMessageMatcher(1)), (MuleSession) Mockito.any()))
            .thenReturn(expectedMuleMessage);

        Exception e = new IllegalArgumentException("boom");

        this.strategy.exceptionThrown(e);

        Mockito.verify(this.router).route(Mockito.argThat(new MuleMessageMatcher(1)),
            (MuleSession) Mockito.any());
        assertEquals(1, this.message.getPayload());
    }

    public void testRouteRequestAndResponseTransformers() throws Exception
    {
        MuleMessage expectedMuleMessage = this.message;
        Mockito.when(
            this.router.route(Mockito.argThat(new MuleMessageMatcher(1)), (MuleSession) Mockito.any()))
            .thenReturn(expectedMuleMessage);

        Exception e = new IllegalArgumentException("boom");

        this.strategy.exceptionThrown(e);

        Mockito.verify(this.router).route(Mockito.argThat(new MuleMessageMatcher(1)),
            (MuleSession) Mockito.any());
        assertEquals(1, this.message.getPayload());
    }
}
