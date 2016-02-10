/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.operation;

import static org.mule.extension.jms.internal.function.JmsSupplier.fromJmsSupplier;
import org.mule.api.MuleContext;
import org.mule.api.temporary.MuleMessage;
import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.param.Connection;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.annotation.api.param.UseConfig;
import org.mule.extension.jms.api.AckMode;
import org.mule.extension.jms.api.JmsConnector;
import org.mule.extension.jms.api.message.JmsAttributes;
import org.mule.extension.jms.internal.JmsMessageUtils;
import org.mule.extension.jms.internal.message.JmsMuleMessageFactory;
import org.mule.extension.jms.internal.support.JmsSupport;

import java.io.IOException;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;


public class JmsConsume
{

    private DestinationType defaultDestinationType = new QueueDestinationType();
    private JmsMuleMessageFactory messageFactory = new JmsMuleMessageFactory();

    @Inject
    private MuleContext muleContext;

    @Operation
    public MuleMessage<Object, JmsAttributes> consume(@Connection JmsConnection connection,
                        @UseConfig JmsConnector jmsConnector,
                        //TODO change to use default values using defautlValoues Optional attribute
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
                session = connection.createSession(convertAckMode(resolveAckMode(jmsConnector.getAckMode(), ackMode), false));
                destinationType = resolveDestinationType(destinationType);
                Destination jmsDestination = connection.getJmsSupport().createDestination(session, destination, destinationType.isTopic());
                java.util.Optional<String> durableName = java.util.Optional.ofNullable(destinationType.isTopic() ? destinationType.getDurableSubscriptionName() : null);
                consumer = connection.getJmsSupport().createConsumer(session, jmsDestination, selector, jmsConnector.isNoLocal(), durableName, destinationType.isTopic());
                Message receive = resolveConsumeMessage(maximumWaitTime, consumer).get();
                return convertJmsMessageToMuleMessage(receive, connection.getJmsSupport().getSpecification());
            }
            finally
            {
                connection.closeQuietly(consumer);
                connection.closeQuietly(session);
            }
        }
        catch (Exception e)
        {
            //TODO throw proper exception
            throw new RuntimeException(e);
        }
    }

    private AckMode resolveAckMode(AckMode configAckMode, AckMode operationAckMode)
    {
        if (operationAckMode != null)
        {
            return operationAckMode;
        }
        return configAckMode;
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

    private DestinationType resolveDestinationType(DestinationType destinationType)
    {
        return destinationType != null ? destinationType : defaultDestinationType;
    }

    private MuleMessage convertJmsMessageToMuleMessage(Message message, JmsSupport.JmsSpecification specification) throws IOException, JMSException
    {
        return messageFactory.createMessage(message, specification, muleContext);
    }

}
