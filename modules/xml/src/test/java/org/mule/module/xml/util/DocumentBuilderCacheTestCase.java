/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.xml.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;

public class DocumentBuilderCacheTestCase
{
    @Test
    public void returnSameInstanceUsingSameFactory() throws ParserConfigurationException
    {
        DocumentBuilder firstDocumentBuilder = XMLUtils.getDocumentBuilder(DocumentBuilderFactory.newInstance());
        DocumentBuilder secondDocumentBuilder = XMLUtils.getDocumentBuilder(DocumentBuilderFactory.newInstance());

        assertThat(firstDocumentBuilder, is(sameInstance(secondDocumentBuilder)));
    }

    @Test
    public void returnDifferentInstanceForDifferentFactory() throws ParserConfigurationException
    {
        DocumentBuilder firstDocumentBuilder = XMLUtils.getDocumentBuilder(new DocumentBuilderFactoryImpl());
        DocumentBuilder secondDocumentBuilder = XMLUtils.getDocumentBuilder(new MyDocumentBuilderFactory());

        assertThat(firstDocumentBuilder, is(not((sameInstance(secondDocumentBuilder)))));
    }

    @Test
    public void returnDifferentInstanceForFactoryWithDifferentConfiguration() throws ParserConfigurationException
    {
        DocumentBuilderFactory documentFactoryBuilder = DocumentBuilderFactory.newInstance();
        DocumentBuilder firstDocumentBuilder = XMLUtils.getDocumentBuilder(documentFactoryBuilder);
        DocumentBuilderFactory documentFactoryBuilderWithNamespaceAware = DocumentBuilderFactory.newInstance();
        documentFactoryBuilderWithNamespaceAware.setNamespaceAware(true);
        DocumentBuilder secondDocumentBuilder = XMLUtils.getDocumentBuilder(documentFactoryBuilderWithNamespaceAware);

        assertThat(firstDocumentBuilder, is(not((sameInstance(secondDocumentBuilder)))));
    }

    private class MyDocumentBuilderFactory extends DocumentBuilderFactoryImpl
    {

    }
}
