/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.mule.api.temporary.MuleMessage;
import org.mule.extension.jms.api.message.JmsAttributes;

import javax.jms.Message;

import org.junit.Test;

public class JmsSubscriberTestCase extends AbstractJmsTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "jms-subscriber-test-case.xml";
    }

    @Test
    public void minimalConfig() throws Exception
    {
        String textMessage = "my message";
        sendMessageToQueue("myQueue", textMessage);
        MuleMessage message = muleContext.getClient().request("test://out", RECEIVE_TIMEOUT);
        assertThat(message.getPayload(), is("my message"));
        JmsAttributes jmsAttributes = (JmsAttributes) message.getAttributes();
        assertThat(jmsAttributes.getHeaders().getJMSMessageID(), notNullValue());
        assertThat(jmsAttributes.getHeaders().getJMSDeliveryMode(), is(Message.DEFAULT_DELIVERY_MODE));
        assertThat(jmsAttributes.getHeaders().getJMSRedelivered(), is(false));
        assertThat(jmsAttributes.getHeaders().getJMSReplyTo(), nullValue());
        assertThat(jmsAttributes.getHeaders().getJMSDestination(), notNullValue());
        assertThat(jmsAttributes.getProperties().isEmpty(), is(true));
        assertThat(jmsAttributes.getProperties().getJmsProperties().isEmpty(), is(true));
        assertThat(jmsAttributes.getProperties().getJmsxProperties().isEmpty(), is(true));
        assertThat(jmsAttributes.getProperties().getUserProperties().isEmpty(), is(true));
    }

}
