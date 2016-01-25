/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.operation;

import org.mule.api.temporary.MuleMessage;
import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.param.Connection;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.annotation.api.param.UseConfig;
import org.mule.extension.jms.api.JmsConnector;

import java.io.Serializable;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class JmsPublish
{

    //TODO add transaction support
    //TODO persistentDelivery, priority, timeToLive should have the value inherited from the config if no value is provided
    @Operation
    public void publish(@Connection JmsConnection connection,
                 String destination,
                 @Optional boolean persistentDeliveryParameter,
                 @Optional DestinationType destinationType,
                 @Optional MessageBuilder messageBuilder,
                 @Optional Integer priority,
                 @Optional Long timeToLive,
                 MuleMessage<Object, Serializable> muleMessage) throws Exception
    {
        Session session = null;
        MessageProducer producer = null;
        try
        {
            session = connection.createSession(JmsConnection.AckMode.AUTO);
            Destination jmsDestination = connection.getJmsSupport().createDestinationFromAddress(session, destination, destinationType.isTopic());
            producer = connection.getJmsSupport().createProducer(session, jmsDestination, destinationType.isTopic());
            Message message = messageBuilder.build(session, muleMessage);
            connection.getJmsSupport().send(producer, message, persistentDeliveryParameter, priority, timeToLive, destinationType.isTopic());
        }
        finally
        {
            connection.closeQuietly(producer);
            connection.closeQuietly(session);
        }
    }

}
