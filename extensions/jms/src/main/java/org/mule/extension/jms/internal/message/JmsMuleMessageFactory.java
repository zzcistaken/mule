/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.message;

import org.mule.DefaultMuleMessage;
import org.mule.api.temporary.MuleMessage;
import org.mule.extension.jms.api.message.JmsHeaders;
import org.mule.extension.jms.internal.JmsMessageUtils;
import org.mule.extension.jms.internal.support.JmsSupport;
import org.mule.util.ClassUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsMuleMessageFactory
{

    private static final Logger logger = LoggerFactory.getLogger(JmsMuleMessageFactory.class);

    public MuleMessage createMessage(Message message, JmsSupport.JmsSpecification specification) throws IOException, JMSException
    {
        DefaultJmsAttributes jmsAttributes = new DefaultJmsAttributes(createMessageProperties(message), createJmsHeades(message));
        Object payload = getPayload(message, specification);
        DefaultMuleMessage defaultMuleMessage = new DefaultMuleMessage(payload, jmsAttributes);
        return defaultMuleMessage;
    }

    private JmsHeaders createJmsHeades(Message jmsMessage)
    {
        DefaultJmsHeaders.Builder headersBuilder = new DefaultJmsHeaders.Builder();
        addCorrelationProperties(jmsMessage, headersBuilder);
        addDeliveryModeProperty(jmsMessage, headersBuilder);
        addDestinationProperty(jmsMessage, headersBuilder);
        addExpirationProperty(jmsMessage, headersBuilder);
        addMessageIdProperty(jmsMessage, headersBuilder);
        addPriorityProperty(jmsMessage, headersBuilder);
        addRedeliveredProperty(jmsMessage, headersBuilder);
        addJMSReplyTo(jmsMessage, headersBuilder);
        addTimestampProperty(jmsMessage, headersBuilder);
        addTypeProperty(jmsMessage, headersBuilder);
        return headersBuilder.build();
    }

    private Object getPayload(Message message, JmsSupport.JmsSpecification specification) throws IOException, JMSException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Message type received is: " +
                         ClassUtils.getSimpleName(message.getClass()));
        }
        return JmsMessageUtils.toObject(message, specification);
    }

    private static Map<String, Object> createMessageProperties(Message jmsMessage)
    {
        Map<String, Object> properties = new HashMap<>();
        try
        {
            Enumeration<?> e = jmsMessage.getPropertyNames();
            while (e.hasMoreElements())
            {
                String key = (String) e.nextElement();
                try
                {
                    Object value = jmsMessage.getObjectProperty(key);
                    if (value != null)
                    {
                        properties.put(key, value);
                    }
                }
                catch (JMSException e1)
                {
                    // ignored
                }
            }
        }
        catch (JMSException e1)
        {
            // ignored
        }
        return properties;
    }

    private void addTypeProperty(Message jmsMessage, DefaultJmsHeaders.Builder jmsHeadersBuilder)
    {
        try
        {
            String value = jmsMessage.getJMSType();
            if (value != null)
            {
                jmsHeadersBuilder.setType(value);
            }
        }
        catch (JMSException e)
        {
            // ignored
        }
    }

    private void addTimestampProperty(Message jmsMessage, DefaultJmsHeaders.Builder jmsHeadersBuilder)
    {
        try
        {
            long value = jmsMessage.getJMSTimestamp();
            jmsHeadersBuilder.setTimestamp(Long.valueOf(value));
        }
        catch (JMSException e)
        {
            // ignored
        }
    }

    private void addJMSReplyTo(Message jmsMessage, DefaultJmsHeaders.Builder jmsHeadersBuilder)
    {
        try
        {
            Destination replyTo = jmsMessage.getJMSReplyTo();
            if (replyTo != null)
            {
                jmsHeadersBuilder.setReplyTo(replyTo);
            }

            //TODO here old code set the reply to into the MuleMessage, verify if we need to do something else
        }
        catch (JMSException e)
        {
            // ignored
        }
    }

    private void addRedeliveredProperty(Message jmsMessage, DefaultJmsHeaders.Builder jmsHeadersBuilder)
    {
        try
        {
            boolean value = jmsMessage.getJMSRedelivered();
            jmsHeadersBuilder.setRedelivered(Boolean.valueOf(value));
        }
        catch (JMSException e)
        {
            // ignored
        }
    }

    private void addPriorityProperty(Message jmsMessage, DefaultJmsHeaders.Builder jmsHeadersBuilder)
    {
        try
        {
            int value = jmsMessage.getJMSPriority();
            jmsHeadersBuilder.setPriority(Integer.valueOf(value));
        }
        catch (JMSException e)
        {
            // ignored
        }
    }

    private void addMessageIdProperty(Message jmsMessage, DefaultJmsHeaders.Builder jmsHeadersBuilder)
    {
        try
        {
            String value = jmsMessage.getJMSMessageID();
            if (value != null)
            {
                jmsHeadersBuilder.setMessageId(value);
                //TODO here mule sets the MULE_MESSAGE_ID see if we have to do somthing
            }
        }
        catch (JMSException e)
        {
            // ignored
        }
    }

    private void addExpirationProperty(Message jmsMessage, DefaultJmsHeaders.Builder jmsHeadersBuilder)
    {
        try
        {
            long value = jmsMessage.getJMSExpiration();
            jmsHeadersBuilder.setExpiration(Long.valueOf(value));
        }
        catch (JMSException e)
        {
            // ignored
        }
    }

    private void addDestinationProperty(Message jmsMessage, DefaultJmsHeaders.Builder jmsHeadersBuilder)
    {
        try
        {
            Destination value = jmsMessage.getJMSDestination();
            if (value != null)
            {
                jmsHeadersBuilder.setDestination(value);
            }
        }
        catch (JMSException e)
        {
            // ignored
        }
    }

    private void addDeliveryModeProperty(Message jmsMessage, DefaultJmsHeaders.Builder jmsHeadersBuilder)
    {
        try
        {
            int value = jmsMessage.getJMSDeliveryMode();
            jmsHeadersBuilder.setDeliveryMode(Integer.valueOf(value));
        }
        catch (JMSException e)
        {
            // ignored
        }
    }

    private void addCorrelationProperties(Message jmsMessage, DefaultJmsHeaders.Builder jmsHeadersBuilder)
    {
        try
        {
            String value = jmsMessage.getJMSCorrelationID();
            if (value != null)
            {
                jmsHeadersBuilder.setCorrelactionId(value);
                //TODO previously here the MULE_CORRELATION_ID was set also, see what to do with that.
            }
        }
        catch (JMSException e)
        {
            // ignored
        }
    }

}
