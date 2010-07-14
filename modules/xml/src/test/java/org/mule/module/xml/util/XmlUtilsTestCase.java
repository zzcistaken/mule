/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.xml.util;

import org.mule.DefaultMuleMessage;
import org.mule.module.xml.expression.JXPathExpressionEvaluator;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.dom4j.DocumentException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtilsTestCase extends AbstractMuleTestCase
{

    private static final String SIMPLE_XML = "simple.xml";

    public void testResolvesSimpleXmlFromDom4jDocument() throws IOException, DocumentException
    {
        org.dom4j.Document document = XMLTestUtils.toDom4jDocument(SIMPLE_XML);
        doSimpleTest(document);
    }

    public void testResolvesSimpleXmlFromW3cDocument() throws IOException, SAXException, ParserConfigurationException
    {
        org.w3c.dom.Document document = XMLTestUtils.toW3cDocument(SIMPLE_XML);
        doSimpleTest(document);
    }

    public void testResolvesSimpleXmlFromInputSource() throws IOException
    {
        InputSource payload = XMLTestUtils.toInputSource(SIMPLE_XML);
        doSimpleTest(payload);
    }

    public void testResolvesSimpleXmlFromSource() throws Exception
    {
        Source payload = XMLTestUtils.toSource(SIMPLE_XML);
        doSimpleTest(payload);
    }

    public void testResolvesSimpleXmlFromXmlStreamReader() throws XMLStreamException, IOException
    {
        XMLStreamReader payload = XMLTestUtils.toXmlStreamReader(SIMPLE_XML);
        doSimpleTest(payload);
    }

    public void testResolvesSimpleXmlFromInputStream() throws IOException
    {
        InputStream payload = XMLTestUtils.toInputStream(SIMPLE_XML);
        doSimpleTest(payload);
    }

    public void testResolvesSimpleXmlFromString() throws IOException, SAXException, ParserConfigurationException
    {
        String payload = XMLTestUtils.toString(SIMPLE_XML);
        doSimpleTest(payload);
    }

    public void testResolvesSimpleXmlFromByteArray() throws IOException
    {
        byte[] payload = XMLTestUtils.toString(SIMPLE_XML).getBytes();
        doSimpleTest(payload);
    }

    public void testResolvesSimpleXmlFromFile() throws IOException
    {
        URL asUrl = IOUtils.getResourceAsUrl(SIMPLE_XML, getClass());
        File payload = new File(asUrl.getFile());
        doSimpleTest(payload);
    }

    private void doSimpleTest(Object payload)
    {
        DefaultMuleMessage msg = new DefaultMuleMessage(payload);

        JXPathExpressionEvaluator e = new JXPathExpressionEvaluator();
        Object value = e.evaluate("/just", msg);

        assertTrue(value instanceof String);
        assertEquals("testing", (String) value);
    }
}
