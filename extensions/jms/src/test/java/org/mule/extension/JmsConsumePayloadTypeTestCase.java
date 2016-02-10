/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import org.mule.api.temporary.MuleMessage;

import javax.jms.Message;
import javax.jms.StreamMessage;

public class JmsConsumePayloadTypeTestCase extends AbstractPayloadTypeTestCase
{

    public static final String MINIMAL_CONFIG_FLOW_NAME = "minimalConfig";

    @Override
    protected String getConfigFile()
    {
        return "jms-consume-test-case.xml";
    }

    @Override
    protected MuleMessage sendAndReceiveMessage(Object payload) throws Exception
    {
        sendMessageToQueue("myQueue", payload);
        return runFlow("minimalConfig", payload).getMessage();
    }

    @Override
    protected MuleMessage sendAndReceiveStreamMessage(int intValue, boolean booleanValue) throws Exception
    {
        getJmsTemplate().send("myQueue", session -> {
            StreamMessage streamMessage = session.createStreamMessage();
            streamMessage.writeInt(new Integer(10));
            streamMessage.writeBoolean(false);
            return streamMessage;
        });
        return runFlow(MINIMAL_CONFIG_FLOW_NAME).getMessage();
    }

    @Override
    protected MuleMessage sendAndReceiveEmptyMessage() throws Exception
    {
        getJmsTemplate().send("myQueue", session -> {
            Message message = session.createMessage();
            return message;
        });
        return runFlow(MINIMAL_CONFIG_FLOW_NAME).getMessage();
    }

}
