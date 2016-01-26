/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.source;

import org.mule.api.MuleContext;
import org.mule.api.execution.CompletionHandler;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.temporary.MuleMessage;
import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.param.Connection;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.api.runtime.source.Source;
import org.mule.extension.jms.api.AckMode;
import org.mule.extension.jms.api.message.JmsAttributes;
import org.mule.extension.jms.internal.message.JmsMuleMessageFactory;
import org.mule.extension.jms.internal.operation.DestinationType;
import org.mule.extension.jms.internal.operation.JmsConnection;
import org.mule.extension.jms.internal.operation.QueueDestinationType;

import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

public class Subscriber extends Source<Object, JmsAttributes> implements Initialisable
{

    @Parameter
    @Optional(defaultValue = "AUTO")
    private AckMode ackMode;

    @Parameter
    @Optional
    private Integer maxRedelivery;

    @Parameter
    @Optional(defaultValue = "false")
    private boolean noLocal;

    @Parameter
    @Optional
    private String selector;

    @Parameter
    private String destination;

    @Parameter
    @Optional
    private DestinationType destinationType = new QueueDestinationType();

    @Connection
    private JmsConnection connection;

    @Inject
    private MuleContext muleContext;

    private JmsMuleMessageFactory jmsMuleMessageFactory = new JmsMuleMessageFactory();
    private MessageConsumer consumer;
    private Session session;

    @Override
    public void start()
    {
        try
        {
            session = connection.createSession(convertAckMode(ackMode));
            Destination jmsDestination = connection.getJmsSupport().createDestination(session, destination, destinationType.isTopic());
            java.util.Optional<String> durableName = java.util.Optional.ofNullable(destinationType.isTopic() && destinationType.useDurableTopicSubscription() ? destinationType.getDurableSubscriptionName() : null);
            consumer = connection.getJmsSupport().createConsumer(session, jmsDestination, selector, noLocal, durableName, destinationType.isTopic());
            consumer.setMessageListener(new MessageListener()
            {
                @Override
                public void onMessage(Message message)
                {
                    if (ackMode.equals(AckMode.NONE))
                    {
                        try
                        {
                            message.acknowledge();
                        }
                        catch (JMSException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                    try
                    {
                        sourceContext.getMessageHandler().handle(jmsMuleMessageFactory.createMessage(message, connection.getJmsSupport().getSpecification(), muleContext), new CompletionHandler<MuleMessage<Object,JmsAttributes>, Exception>()
                        {
                            @Override
                            public void onCompletion(MuleMessage muleMessage)
                            {
                                if (ackMode.equals(AckMode.AUTO))
                                {
                                    try
                                    {
                                        message.acknowledge();
                                    }
                                    catch (JMSException e)
                                    {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Exception exception)
                            {
                                //Nothing to do.
                            }
                        });
                    }
                    catch(Exception e)
                    {
                        sourceContext.getExceptionCallback().onException(e);
                    }
                }
            });
        }
        catch (Exception e)
        {
            connection.closeQuietly(consumer);
            connection.closeQuietly(session);
            sourceContext.getExceptionCallback().onException(e);
        }
    }

    private JmsConnection.AckMode convertAckMode(AckMode ackMode)
    {
        if (ackMode.equals(AckMode.MANUAL))
        {
            return JmsConnection.AckMode.CLIENT;
        }
        else if (ackMode.equals(AckMode.AUTO))
        {
            return JmsConnection.AckMode.AUTO;
        }
        else if (ackMode.equals(AckMode.DUPS_OK))
        {
            return JmsConnection.AckMode.DUPS_OK;
        }
        else
        {
            throw new RuntimeException();
        }
    }

    @Override
    public void stop()
    {
        connection.closeQuietly(consumer);
        connection.closeQuietly(session);
    }

    @Override
    public void initialise() throws InitialisationException
    {
        //TODO MULE-9350 - remove once SDK does not overrides default value with null
        if (destinationType == null)
        {
            destinationType = new QueueDestinationType();
        }
    }
}
