/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.operation;

import static java.util.Optional.ofNullable;
import static javax.jms.Message.DEFAULT_DELIVERY_DELAY;
import org.mule.api.temporary.MuleMessage;
import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.param.Connection;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.annotation.api.param.UseConfig;
import org.mule.extension.jms.api.JmsConnector;
import org.mule.extension.jms.internal.support.JmsSupport;
import org.mule.util.Preconditions;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class JmsPublish
{

    private MessageBuilder defaultMessageBuilder = new MessageBuilder();
    //TODO change to be uset using defaultValues
    private DestinationType defaultDestinationType = new QueueDestinationType();

    private static final Method setDeliveryDelayMethod =
            ClassUtils.getMethodIfAvailable(MessageProducer.class, "setDeliveryDelay", long.class);

    //TODO add transaction support
    //TODO persistentDelivery, priority, timeToLive should have the value inherited from the config if no value is provided
    @Operation
    public void publish(@Connection JmsConnection connection,
                 @UseConfig JmsConnector jmsConnector,
                 String destination,
                 @Optional boolean persistentDelivery,
                 @Optional DestinationType destinationType,
                 @Optional MessageBuilder messageBuilder,
                 @Optional Integer priority,
                 @Optional Long timeToLive,
                 MuleMessage<Object, Serializable> muleMessage,
                 @Optional Long deliveryDelay) throws Exception
    {
        boolean deliveryDelayOnlyConfiguredForJms2 = connection.getJmsSupport().getSpecification().equals(JmsSupport.JmsSpecification.JMS_2_0) || deliveryDelay == null;
        Preconditions.checkState(deliveryDelayOnlyConfiguredForJms2, "Delivery delay is only supported when working with JMS spec 2.0");
        Session session = null;
        MessageProducer producer = null;
        try
        {
            session = connection.createSession(JmsConnection.AckMode.AUTO);
            destinationType = resolveDestinationType(destinationType);
            priority = resolvePriority(jmsConnector.getPriority(), priority);
            timeToLive = resolveTimeToLive(jmsConnector.getTimeToLive(), timeToLive);
            Destination jmsDestination = connection.getJmsSupport().createDestinationFromAddress(session, destination, destinationType.isTopic());
            producer = connection.getJmsSupport().createProducer(session, jmsDestination, destinationType.isTopic());
            deliveryDelay = resolveDeliveryDelay(deliveryDelay);
            ReflectionUtils.invokeMethod(setDeliveryDelayMethod, producer, deliveryDelay);
            messageBuilder = ofNullable(messageBuilder).orElse(defaultMessageBuilder);
            Message message = messageBuilder.build(session, muleMessage);
            connection.getJmsSupport().send(producer, message, persistentDelivery, priority, timeToLive, destinationType.isTopic());
        }
        finally
        {
            connection.closeQuietly(producer);
            connection.closeQuietly(session);
        }
    }

    private Long resolveDeliveryDelay(Long deliveryDelay)
    {
        return deliveryDelay != null ? deliveryDelay : DEFAULT_DELIVERY_DELAY;
    }

    private Long resolveTimeToLive(Long jmsConfigTimeToLive, Long operationTimeToLive)
    {
        return operationTimeToLive == null ? jmsConfigTimeToLive : operationTimeToLive;
    }

    private Integer resolvePriority(Integer jmsConfigPriority, Integer operationPriority)
    {
        return operationPriority == null ? jmsConfigPriority : operationPriority;
    }

    private DestinationType resolveDestinationType(DestinationType destinationType)
    {
        return destinationType != null ? destinationType : defaultDestinationType;
    }

}
