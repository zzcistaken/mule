/*
 * $Id$
 *
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.cxf;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

import org.custommonkey.xmlunit.XMLAssert;

public class CxfWithWsdlIncludingHeadersTestCase extends FunctionalTestCase
{

    private static final String REQUEST_MESSAGE =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:sim=\"http://www.example.org/simplewithheader/\">" +
            "<soapenv:Header>" +
            "<sim:header><headerparam>?</headerparam>" +
            "</sim:header></soapenv:Header>" +
            "<soapenv:Body>" +
            "<sim:NewOperationRequest>" +
            "<in>?</in></sim:NewOperationRequest>" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";

    private MuleClient client;

    @Override
    protected String getConfigResources()
    {
        return "cxf-wsdl-including-headers-config.xml";
    }

    public void doSetUp() throws MuleException
    {
        client = new MuleClient();
    }

    public void testEnvelopePayload() throws Exception
    {
        MuleMessage result = client.send("http://localhost:9080/services/Sample", new DefaultMuleMessage(REQUEST_MESSAGE));

        XMLAssert.assertEquals(REQUEST_MESSAGE, result.getPayloadAsString());
    }

    public void testBodyPayload() throws Exception
    {
        String expectedAnswer =
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body><sim:NewOperationRequest xmlns:sim=\"http://www.example.org/simplewithheader/\">" +
                "<in>?</in>" +
                "</sim:NewOperationRequest>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        MuleMessage result = client.send("http://localhost:9080/services/Sample2", new DefaultMuleMessage(REQUEST_MESSAGE));

        XMLAssert.assertEquals(expectedAnswer, result.getPayloadAsString());
    }

    public void testBodyPayloadWithTransformer() throws Exception
    {
        String expectedAnswer =
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<sim:NewOperationRequest xmlns:sim=\"http://www.example.org/simplewithheader/\">" +
                "<in>?</in>" +
                "</sim:NewOperationRequest>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        MuleMessage result = client.send("http://localhost:9080/services/Sample4", new DefaultMuleMessage(REQUEST_MESSAGE));

        XMLAssert.assertEquals(expectedAnswer, result.getPayloadAsString());
    }
}
