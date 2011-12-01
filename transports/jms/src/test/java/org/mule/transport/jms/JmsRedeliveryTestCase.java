/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.jms;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.context.notification.ExceptionNotificationListener;
import org.mule.api.context.notification.ServerNotification;
import org.mule.context.notification.ExceptionNotification;
import org.mule.context.notification.NotificationException;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.tck.exceptions.FunctionalTestException;
import org.mule.tck.functional.CounterCallback;
import org.mule.tck.functional.FunctionalTestComponent;
import org.mule.util.concurrent.Latch;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class JmsRedeliveryTestCase extends FunctionalTestCase
{

    private final int timeout = getTimeoutSecs() * 1000 / 4;

    private Latch redeliveryExceptionFire = new Latch();
    private MuleClient client;
    private CounterCallback callback;

    @Override
    protected String getConfigResources()
    {
        return "jms-redelivery.xml";
    }

    @Override
    protected void doSetUp() throws Exception
    {
        client = new MuleClient();
        callback = createEventCallback();
        registerEventListener(redeliveryExceptionFire);
    }

    public void testRedelivery() throws Exception
    {
        String destination = "jms://in1?connector=jmsConnectorLimitedRedelivery";
        purgeQueue(destination);
        setupCallback("Bouncer1");

        client.dispatch(destination, "test", null);

        assertTrue(redeliveryExceptionFire.await(timeout, TimeUnit.MILLISECONDS));
        assertEquals("MessageRedeliveredException never fired.", 0, redeliveryExceptionFire.getCount());
        assertEquals("Wrong number of delivery attempts", 4, callback.getCallbackCount());
    }

    public void testInfiniteRedelivery() throws Exception
    {
        String destination = "jms://in2?connector=jmsConnectorInfiniteRedelivery";
        purgeQueue(destination);
        setupCallback("Bouncer2");

        client.dispatch(destination, "test", null);

        assertFalse(redeliveryExceptionFire.await(timeout, TimeUnit.MILLISECONDS));
        assertEquals("MessageRedeliveredException was fired.", 1, redeliveryExceptionFire.getCount());
        assertTrue(callback.getCallbackCount() > 6);
    }

    public void testNoRedelivery() throws Exception
    {
        purgeQueue("jms://in3?connector=jmsConnectorNoRedelivery");
        setupCallback("Bouncer3");

        client.dispatch("jms://in3?connector=jmsConnectorNoRedelivery", "test", null);

        assertTrue(redeliveryExceptionFire.await(timeout, TimeUnit.MILLISECONDS));
        assertEquals("MessageRedeliveredException never fired.", 0, redeliveryExceptionFire.getCount());
        assertEquals(1, callback.getCallbackCount());
    }

    private void setupCallback(String serviceName) throws Exception
    {
        FunctionalTestComponent component = (FunctionalTestComponent) getComponent(serviceName);
        component.setEventCallback(callback);
    }

    private void registerEventListener(final Latch redeliveryExceptionFire) throws NotificationException
    {
        muleContext.registerListener(new ExceptionNotificationListener()
        {
            public void onNotification(ServerNotification notification)
            {
                ExceptionNotification n = (ExceptionNotification) notification;
                if (n.getException() instanceof MessageRedeliveredException)
                {
                    redeliveryExceptionFire.countDown();
                }
            }
        });
    }

    private CounterCallback createEventCallback()
    {
        // enhance the counter callback to count, then throw an exception
        return new CounterCallback()
        {
            @Override
            public void eventReceived(MuleEventContext context, Object Component) throws Exception
            {
                final int count = incCallbackCount();
                logger.info("Message Delivery Count is: " + count);
                throw new FunctionalTestException();
            }
        };
    }

    private void purgeQueue(String destination) throws MuleException
    {
        // required if broker is not restarted with the test - it tries to deliver those messages to the client
        // purge the queue
        while (client.request(destination, 1000) != null)
        {
            logger.warn("Destination " + destination + " isn't empty, draining it");
        }
    }
}