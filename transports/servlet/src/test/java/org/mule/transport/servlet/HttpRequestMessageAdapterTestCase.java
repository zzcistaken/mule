/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.servlet;

import org.mule.DefaultMuleMessage;
import org.mule.api.MessagingException;
import org.mule.api.MuleMessage;
import org.mule.api.transport.MessageAdapter;
import org.mule.transport.AbstractMessageAdapterTestCase;
import org.mule.transport.http.HttpConstants;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.iterators.IteratorEnumeration;

public class HttpRequestMessageAdapterTestCase extends AbstractMessageAdapterTestCase
{
    public static HttpServletRequest getMockRequest(String message)
    {
        ClassLoader classLoader = HttpRequestMessageAdapterTestCase.class.getClassLoader();
        InvocationHandler invocationHandler = new TestInvocationHandler(message);
        Object proxy = Proxy.newProxyInstance(classLoader,
            new Class[]{ HttpServletRequest.class }, invocationHandler);
        return (HttpServletRequest) proxy;
    }
    
    public Object getValidMessage() throws Exception
    {
        return getMockRequest("test message");
    }
    
    @Override
    protected void doTestMessageEqualsPayload(Object message, Object payload) throws Exception
    {
        assertTrue(payload instanceof InputStream);
    }

    public MessageAdapter createAdapter(Object payload) throws MessagingException
    {
        return new HttpRequestMessageAdapter(payload);
    }
    
    public void testRequestHeaders() throws Exception
    {
        Object payload = getValidMessage();
        MessageAdapter adapter = createAdapter(payload);
        MuleMessage muleMessage = new DefaultMuleMessage(adapter);
        assertEquals("single-value", muleMessage.getProperty("single-value-key"));
        assertEquals("prefix-value", muleMessage.getProperty("MULE_PREFIX_KEY"));
        
        Object[] expected = new Object[] { "value-one", "value-two" };
        Object[] actual = (Object[]) muleMessage.getProperty("multi-value-key");
        assertTrue(Arrays.equals(expected, actual));
        
        assertEquals("localhost:8080", muleMessage.getProperty(HttpConstants.HEADER_HOST));
    }
    
    private static class TestInvocationHandler implements InvocationHandler
    {
        private String payload;
        private Map<Object, Object> props = new HashMap<Object, Object>();
        private Hashtable<Object, List<?>> headers;

        public TestInvocationHandler(String message)
        {
            super();
            payload = message;
            initHeaders();
        }

        private void initHeaders()
        {
            headers = new Hashtable<Object, List<?>>();
            headers.put("single-value-key", Arrays.asList("single-value"));
            headers.put("X-MULE_PREFIX_KEY", Arrays.asList("prefix-value"));
            headers.put("multi-value-key", Arrays.asList("value-one", "value-two"));
            headers.put(HttpConstants.HEADER_HOST, Arrays.asList("localhost:8080"));
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            String methodName = method.getName();
            if ("getInputStream".equals(methodName))
            {
                ServletInputStream s = new ServletInputStream()
                {
                    ByteArrayInputStream is = new ByteArrayInputStream(payload.getBytes());

                    public int read() throws IOException
                    {
                        return is.read();
                    }
                };
                return s;
            }
            else if ("getAttribute".equals(methodName))
            {
                return props.get(args[0]);
            }
            else if ("setAttribute".equals(methodName))
            {
                props.put(args[0], args[1]);
            }
            else if ("equals".equals(methodName))
            {
                return Boolean.valueOf(payload.equals(args[0].toString()));
            }
            else if ("toString".equals(methodName))
            {
                return payload;
            }
            else if ("getReader".equals(methodName))
            {
                return new BufferedReader(new StringReader(payload.toString()));
            }
            else if ("getAttributeNames".equals(methodName))
            {
                return new Hashtable<Object, Object>().keys();
            }
            else if ("getHeaderNames".equals(methodName))
            {
                return headers.keys();
            }
            else if ("getHeaders".equals(methodName))
            {
                Object key = args[0];
                List<?> values = headers.get(key);
                return new IteratorEnumeration(values.iterator());
            }
            else if ("getHeader".equals(methodName))
            {
                if (args[0].equals(HttpConstants.HEADER_HOST))
                {
                    Object key = args[0];
                    List<?> values = headers.get(key);
                    return values.get(0);
                }
                return null;
            }
            else if ("getLocalPort".equals(methodName))
            {
                return Integer.valueOf(8080);
            }
            return null;
        }
    }
}
