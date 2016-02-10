/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.operation;

import org.mule.api.MuleEvent;
import org.mule.api.temporary.MuleMessage;
import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.jms.internal.JmsMessageUtils;

import java.io.Serializable;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class MessageBuilder
{
    @Parameter
    @Optional(defaultValue = "true")
    private boolean sendContentType;

    @Parameter
    @Optional
    List<MessageProperty> properties;

    public Message build(Session session, MuleMessage<Object, Serializable> muleMessage) throws JMSException
    {
        //TODO review with MG how to deal with properties
        Message message = JmsMessageUtils.toMessage(muleMessage.getPayload(), session);
        if (sendContentType)
        {

        }
        return message;
    }
}
