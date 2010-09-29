/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.transport.cxf;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.tck.testmodels.mule.ExceptionCallback;
import org.mule.tck.testmodels.mule.TestExceptionStrategy;
import org.mule.transport.cxf.CxfConnector;
import org.mule.transport.cxf.support.OutputPayloadInterceptor;
import org.mule.util.concurrent.Latch;

import java.util.concurrent.atomic.AtomicInteger;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class CxfAndXslTransformerOnSoapTestCase extends FunctionalTestCase
{
    final String msg = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:emop=\"http://www.wcs.com/2010/07/14/emop\">"
                       + "  <soapenv:Header>\n"
                       + "    <header UserName=\"nothing\" Password=\"important\"/>\n"
                       + "  </soapenv:Header>\n"
                       + "  <soapenv:Body>\n"
                       + "    <emop:ScratchcardValidateAndPayRequestBody>\n"
                       + "       <ScratchcardNumber>1</ScratchcardNumber>\n"
                       + "       <VirnNumber>2</VirnNumber>\n"
                       + "    </emop:ScratchcardValidateAndPayRequestBody>\n"
                       + "  </soapenv:Body>\n"
                       + "</soapenv:Envelope>";

    private final AtomicInteger connectorExceptionCounter = new AtomicInteger();

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        connectorExceptionCounter.set(0);
    }

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/transport/cxf/scratchcard-service-v1.xml";
    }

    /**
     * This test fails without the change involving the
     * {@link OutputPayloadInterceptor#cleanUpPayload(Object)}. It is a fix for issue
     * MULE-5030.
     * <p>
     * This test also verifies the fix for MULE-5113. The only required change was in
     * the scratchcard-service-v1.xml file: adding payload="envelope" property in the
     * inbound configuration.
     * <p>
     * Both issues are related to the same CXF bug, but they needed different hacks
     * in mule code in order to be fixed.
     * 
     * @throws Exception
     */
    public void testUsesTransformersCorrectly() throws Exception
    {
        CxfConnector conn = (CxfConnector) muleContext.getRegistry().lookupConnector("someCxfConnector");
        TestExceptionStrategy exceptionStrategy = (TestExceptionStrategy) conn.getExceptionListener();

        MuleClient client = new MuleClient();
        MuleMessage result = client.send("http://localhost:28181/ScratchCardServiceV1", msg, null);
        assertNotNull("The result shouln't have been null", result);
        final String payloadAsString = result.getPayloadAsString();
        assertNotNull("The payloadAsString shouln't have been null", payloadAsString);
        assertFalse("There shouldn't be a fault in the payload: " + payloadAsString,
            payloadAsString.contains("<soap:Fault>"));

        final Latch latch = new Latch();
        exceptionStrategy.setExceptionCallback(new ExceptionCallback()
        {
            public void onException(Throwable t)
            {
                connectorExceptionCounter.incrementAndGet();
                latch.countDown();
            }
        });
        latch.await(500, TimeUnit.MILLISECONDS);
        assertEquals("There shouldn't have been any exceptions", 0, connectorExceptionCounter.get());
    }
}


