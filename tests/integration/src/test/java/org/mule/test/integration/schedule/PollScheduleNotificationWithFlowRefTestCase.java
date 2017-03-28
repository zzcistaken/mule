/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.schedule;


import org.mule.api.AnnotatedObject;
import org.mule.api.context.notification.EndpointMessageNotificationListener;
import org.mule.api.context.notification.MessageProcessorNotificationListener;
import org.mule.context.notification.EndpointMessageNotification;
import org.mule.context.notification.MessageProcessorNotification;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.probe.PollingProber;
import org.mule.tck.probe.Probe;
import org.mule.tck.probe.Prober;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;

public class PollScheduleNotificationWithFlowRefTestCase extends FunctionalTestCase
{

    public static final QName NAME = new QName("http://www.mulesoft.org/schema/mule/documentation", "name");
    Prober prober = new PollingProber(5000, 100l);

    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/integration/schedule/poll-notifications-with-flow-ref-config.xml";
    }

    @Test
    public void validateNotificationsAreSent() throws InterruptedException
    {
        final MyListener listener = new MyListener();
        final MyProcessorsListener myProcessorsListener = new MyProcessorsListener();
        muleContext.getNotificationManager().addListener(listener);
        muleContext.getNotificationManager().addListener(myProcessorsListener);
        prober.check(new Probe()
        {
            @Override
            public boolean isSatisfied()
            {
                //For every execution of the poll atleast 3 notifications should be generated. Two for the loggers inside the flow referenced inside the poll , and another for the one outside of it
                int pollExecutions = listener.getNotifications().size();
                return myProcessorsListener.getNotifications().size() >= 3 * pollExecutions && pollExecutions > 1 && "pollName".equals(listener.getNotifications().get(0));
            }

            @Override
            public String describeFailure()
            {
                return "The notification was never sent";
            }
        });

    }

    class MyListener implements EndpointMessageNotificationListener<EndpointMessageNotification>
    {

        List<String> notifications = new ArrayList<String>();

        @Override
        public void onNotification(EndpointMessageNotification notification)
        {
            notifications.add((String) ((AnnotatedObject) notification.getImmutableEndpoint()).getAnnotation(NAME));
        }

        public List<String> getNotifications()
        {
            return notifications;
        }
    }

    class MyProcessorsListener implements MessageProcessorNotificationListener<MessageProcessorNotification>
    {

        List<String> notifications = new ArrayList<String>();

        @Override
        public void onNotification(MessageProcessorNotification notification)
        {
            notifications.add((String) ((AnnotatedObject) notification.getProcessor()).getAnnotation(NAME));
        }

        public List<String> getNotifications()
        {
            return notifications;
        }
    }
}
