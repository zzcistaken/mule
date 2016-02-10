/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import org.mule.api.MuleException;
import org.mule.api.temporary.MuleMessage;

import javax.jms.Message;
import javax.jms.StreamMessage;

public class JmsSubscriberPayloadTypeTestCase extends AbstractPayloadTypeTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "jms-subscriber-test-case.xml";
    }

    @Override
    protected MuleMessage sendAndReceiveMessage(Object payload) throws MuleException
    {
        sendMessageToQueue("myQueue", payload);
        return muleContext.getClient().request("test://out", RECEIVE_TIMEOUT);
    }

    @Override
    protected MuleMessage sendAndReceiveStreamMessage(int intValue, boolean booleanValue) throws MuleException
    {
        getJmsTemplate().send("myQueue", session -> {
            StreamMessage streamMessage = session.createStreamMessage();
            streamMessage.writeInt(new Integer(10));
            streamMessage.writeBoolean(false);
            return streamMessage;
        });
        return muleContext.getClient().request("test://out", RECEIVE_TIMEOUT);
    }

    @Override
    protected MuleMessage sendAndReceiveEmptyMessage() throws MuleException
    {
        getJmsTemplate().send("myQueue", session -> {
            Message message = session.createMessage();
            return message;
        });
        return muleContext.getClient().request("test://out", RECEIVE_TIMEOUT);
    }

}
