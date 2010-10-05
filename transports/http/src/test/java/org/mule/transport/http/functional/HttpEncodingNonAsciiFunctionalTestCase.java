/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.http.functional;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.LocaleMessageHandler;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.tck.functional.EventCallback;
import org.mule.tck.functional.FunctionalTestComponent;
import org.mule.transformer.AbstractTransformer;
import org.mule.transport.http.HttpConnector;
import org.mule.transport.http.HttpConstants;
import org.mule.util.concurrent.Latch;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.Assert;

import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class HttpEncodingNonAsciiFunctionalTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "http-encoding-non-ascii-test.xml";
    }

    public void XXX_testSendByGet() throws Exception
    {
        Object messagePayload = getTestMessage(Locale.JAPAN);
        Map<String, Object> messageProperties = new HashMap<String, Object>();
        messageProperties.put(MuleProperties.MULE_ENCODING_PROPERTY, "ISO-2022-JP");
        doTestSend("GET", messagePayload, messageProperties, "text/plain; charset=ISO-2022-JP");
    }

    public void testSendByPost() throws Exception
    {
        Object messagePayload = getTestMessage(Locale.JAPAN);
        Map<String, Object> messageProperties = new HashMap<String, Object>();
        messageProperties.put(MuleProperties.MULE_ENCODING_PROPERTY, "ISO-2022-JP");
        doTestSend("POST", messagePayload, messageProperties, "text/plain; charset=ISO-2022-JP");
    }

    private void doTestSend(String method, Object messagePayload, Map<String, Object> messageProperties,
        String expectedContentType) throws Exception
    {
        Latch latch = new Latch();

        setupAssertIncomingMessage(method, latch, expectedContentType);

        MuleClient client = new MuleClient(muleContext);
        MuleMessage reply = client.send("vm://sendBy" + method, messagePayload, messageProperties);

        assertTrue(latch.await(RECEIVE_TIMEOUT, TimeUnit.MILLISECONDS));
        assertNotNull(reply);
        assertEquals(expectedContentType, reply.getInvocationProperty(HttpConstants.HEADER_CONTENT_TYPE));
        assertEquals("EUC-JP", reply.getEncoding());
        assertEquals(getTestMessage(Locale.JAPAN) + " Received", reply.getPayloadAsString());
    }

    private void setupAssertIncomingMessage(String method, final CountDownLatch cdl,
        final String expectedContentType) throws Exception
    {
        FunctionalTestComponent ftc = getFunctionalTestComponent("testReceive" + method);
        ftc.setEventCallback(new EventCallback()
        {
            public void eventReceived(MuleEventContext context, Object serviceComponent) throws Exception
            {
                MuleMessage message = context.getMessage();

                Assert.assertEquals(expectedContentType,
                    message.getInboundProperty(HttpConstants.HEADER_CONTENT_TYPE, null));
                Assert.assertEquals("ISO-2022-JP", message.getEncoding());

                Object payload = message.getPayload();
                if (payload instanceof String)
                {
                    assertEquals(getTestMessage(Locale.JAPAN), payload);
                }
                else if (payload instanceof byte[])
                {
                    String payloadString = new String((byte[])payload, message.getEncoding());
                    assertEquals(getTestMessage(Locale.JAPAN), payloadString);
                }
                else
                {
                    fail();
                }

                cdl.countDown();
            }
        });
    }

    public static class ParamMapToString extends AbstractTransformer
    {
        @Override
        protected Object doTransform(Object src, String outputEncoding) throws TransformerException
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>)src;
            return map.get(HttpConnector.DEFAULT_HTTP_GET_BODY_PARAM_PROPERTY);
        }
    }

    String getTestMessage(Locale locale)
    {
        return LocaleMessageHandler.getString("test-data", locale,
            "HttpEncodingNonAsciiFunctionalTestCase.getMessage", new Object[]{});
    }
}
