/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import org.mule.api.MuleException;
import org.mule.api.temporary.MuleMessage;
import org.mule.transport.NullPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.core.Is;
import org.junit.Test;

public abstract class AbstractPayloadTypeTestCase extends AbstractJmsTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "jms-subscriber-test-case.xml";
    }

    /**
     * Sends the payload to mule and receives it within mule.
     * @param payload
     */
    protected abstract MuleMessage sendAndReceiveMessage(Object payload) throws Exception;

    /**
     * Sends the payload to mule and receives it within mule. Only used when sending a {@link javax.jms.StreamMessage}
     */
    protected abstract MuleMessage sendAndReceiveStreamMessage(int intValue, boolean booleanValue) throws Exception;

    /**
     * Sends the payload to mule and receives it within mule. Only used when sending a message without content
     */
    protected abstract MuleMessage sendAndReceiveEmptyMessage() throws Exception;

    @Test
    public void receiveObject() throws Exception
    {
        Integer payload = new Integer(10);
        MuleMessage message = sendAndReceiveMessage(payload);
        assertThat(message.getPayload(), is(payload));
    }

    @Test
    public void receiveMap() throws Exception
    {
        Map<String, String> payload = new HashMap<>();
        payload.put("key", "value");
        MuleMessage message = sendAndReceiveMessage(payload);
        assertThat(message.getPayload(), is(payload));
    }

    @Test
    public void receiveBytes() throws Exception
    {
        byte[] payload = TEST_PAYLOAD.getBytes();
        MuleMessage message = sendAndReceiveMessage(payload);
        assertThat(message.getPayload(), is(payload));
    }

    @Test
    public void receiveStream() throws Exception
    {
        int intValue = 10;
        boolean booleanValue = false;
        MuleMessage message = sendAndReceiveStreamMessage(intValue, booleanValue);
        assertThat(message.getPayload(), instanceOf(List.class));
        List streamMessage = (List) message.getPayload();
        assertThat(streamMessage.get(0), is(intValue));
        assertThat(streamMessage.get(1), is(booleanValue));
    }

    @Test
    public void receiveText() throws Exception
    {
        MuleMessage message = sendAndReceiveMessage(TEST_PAYLOAD);
        assertThat(message.getPayload(), is(TEST_PAYLOAD));
    }

    @Test
    public void receiveNull() throws Exception
    {
        MuleMessage message = sendAndReceiveEmptyMessage();
        assertThat(message.getPayload(), Is.is(NullPayload.getInstance()));
    }


}
