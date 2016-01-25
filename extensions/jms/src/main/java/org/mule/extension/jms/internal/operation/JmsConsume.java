/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.operation;

import static org.mule.extension.jms.internal.function.JmsSupplier.fromJmsSupplier;
import org.mule.api.temporary.MuleMessage;
import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.param.Connection;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.annotation.api.param.UseConfig;
import org.mule.extension.jms.api.AckMode;
import org.mule.extension.jms.api.JmsConnector;

import java.util.function.Supplier;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;


public class JmsConsume
{

    @Operation
    MuleMessage consume(@Connection JmsConnection connection,
                        @UseConfig JmsConnector jmsConnector,
                        @Optional DestinationType destinationType,
                        @Optional AckMode ackMode,
                        @Optional String selector,
                        @Optional String destination,
                        @Optional(defaultValue = "10000") Long maximumWaitTime)
    {

        try
        {
            Session session = null;
            MessageConsumer consumer = null;
            try
            {
                session = connection.createSession(convertAckMode(ackMode, false));
                Destination jmsDestination = connection.getJmsSupport().createDestination(session, destination, destinationType.isTopic());
                consumer = connection.getJmsSupport().createConsumer(session, jmsDestination, selector, jmsConnector.isNoLocal(), destinationType.isTopic() ? destinationType.getDurableSubscriptionName() : null, destinationType.isTopic());
                Message receive = resolveConsumeMessage(maximumWaitTime, consumer).get();
                return convertJmsMessageToMuleMessage(receive);
            }
            finally
            {
                connection.closeQuietly(consumer);
                connection.closeQuietly(session);
            }
        }
        catch (JMSException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Supplier<Message> resolveConsumeMessage(Long maximumWaitTime, MessageConsumer consumer)
    {
        if (maximumWaitTime == -1)
        {
            return fromJmsSupplier(consumer::receive);
        }
        else if (maximumWaitTime == 0)
        {
            return fromJmsSupplier(consumer::receiveNoWait);
        }
        else
        {
            return fromJmsSupplier(() -> consumer.receive(maximumWaitTime));
        }
    }

    private JmsConnection.AckMode convertAckMode(AckMode ackMode, boolean useTransaction)
    {
        if (useTransaction)
        {
            return JmsConnection.AckMode.TRANSACTED;
        }
        else if (ackMode.equals(AckMode.AUTO))
        {
            return JmsConnection.AckMode.AUTO;
        }
        else if (ackMode.equals(AckMode.MANUAL))
        {
            return JmsConnection.AckMode.CLIENT;
        }
        else
        {
            return JmsConnection.AckMode.DUPS_OK;
        }
    }

    private MuleMessage convertJmsMessageToMuleMessage(Message message)
    {
        return null;
    }

}
