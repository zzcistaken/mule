/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.tcp;

import org.mule.ResponseOutputStream;
import org.mule.api.transport.DispatchException;
import org.mule.module.client.MuleClient;
import org.mule.tck.DynamicPortTestCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpSocketsPoolTestCase extends DynamicPortTestCase
{

    protected static String TEST_MESSAGE = "Test TCP Request";


    @Override
    protected String getConfigResources()
    {
        return "tcp-sockets-pool-test.xml";
    }

    public void testExceptionInSendReleasesSocket() throws Exception
    {
        TcpConnector tcpConnector = (TcpConnector) muleContext.getRegistry().lookupConnector("connectorWithException");
        assertNotNull(tcpConnector);
        MuleClient client = new MuleClient(muleContext);
        try
        {
            client.send("clientWithExceptionEndpoint", TEST_MESSAGE, null);
            fail("Dispatch exception was expected");
        }
        catch(DispatchException e)
        {
            // Expected exception
        }
        assertEquals(0, tcpConnector.getSocketsPoolNumActive());
    }

    public void testSocketsPoolSettings() throws Exception
    {
        TcpConnector tcpConnector = (TcpConnector) muleContext.getRegistry().lookupConnector("connectorWithException");
        assertEquals(8, tcpConnector.getSocketsPoolMaxActive());
        assertEquals(8, tcpConnector.getSocketsPoolMaxIdle());
        assertEquals(3000, tcpConnector.getSocketsPoolMaxWait());
    }

    public void testSocketsPoolDefaultSettings() throws Exception
    {
        TcpConnector tcpConnector = (TcpConnector) muleContext.getRegistry().lookupConnector("tcpConnector");
        int maxActive = tcpConnector.getDispatcherThreadingProfile().getMaxThreadsActive();
        assertEquals(maxActive, tcpConnector.getSocketsPoolMaxActive());
        assertEquals(maxActive, tcpConnector.getSocketsPoolMaxIdle());
        assertEquals(TcpConnector.DEFAULT_WAIT_TIMEOUT, tcpConnector.getSocketMaxWait());
    }

    public static class MockTcpProtocol implements TcpProtocol
    {
        public ResponseOutputStream createResponse(Socket socket) throws IOException
        {
            throw new UnsupportedOperationException("createResponse");
        }

        public Object read(InputStream is) throws IOException
        {
            throw new UnsupportedOperationException("read");
        }

        public void write(OutputStream os, Object data) throws IOException
        {
            throw new UnsupportedOperationException("write");
        }
    }

    @Override
    protected int getNumPortsToFind()
    {
        return 1;
    }

}
