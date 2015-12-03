/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.internal.capability.studio.editor;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mule.api.registry.ServiceRegistry;
import org.mule.extension.api.introspection.ExtensionFactory;
import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.extension.api.introspection.declaration.fluent.Descriptor;
import org.mule.extension.api.introspection.declaration.spi.ModelEnricher;
import org.mule.module.extension.internal.DefaultDescribingContext;
import org.mule.module.extension.internal.capability.xml.XmlModelEnricher;
import org.mule.module.extension.internal.introspection.AnnotationsBasedDescriber;
import org.mule.module.extension.internal.introspection.DefaultExtensionFactory;
import org.mule.module.extension.studio.model.Namespace;
import org.mule.registry.SpiServiceRegistry;
import org.mule.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Created by pablocabrera on 12/1/15.
 */
public abstract class AbstractEditorGeneratorTest
{
    private ExtensionFactory extensionFactory;
    private StudioEditorGenerator generator;

    @Before
    public void before()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        ServiceRegistry serviceRegistry = mock(ServiceRegistry.class);
        when(serviceRegistry.lookupProviders(ModelEnricher.class, classLoader)).thenReturn(asList(new XmlModelEnricher(), new StudioEditorModelEnricher()));

        extensionFactory = new DefaultExtensionFactory(new SpiServiceRegistry(), getClass().getClassLoader());

    }

    @Test
    public void testGeneration() throws IOException, SAXException, JAXBException
    {
        Descriptor descriptor = new AnnotationsBasedDescriber(getExtensionUnderTest()).describe(new DefaultDescribingContext()).getRootDeclaration();
        ExtensionModel extensionModel = extensionFactory.createFrom(descriptor);
        generator = StudioEditorGenerator.newStudioEditorGenerator(extensionModel);
        Namespace editor=generator.build();
        String editorContent="";
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(Namespace.class);
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(editor, outputStream);
            editorContent=outputStream.toString();
        }
        XMLUnit.setIgnoreWhitespace(true);

        String expectedEditor = IOUtils.getResourceAsString(getExpectedContentFileName(), getClass());
        System.out.println(editorContent);
        assertThat(XMLUnit.compareXML(expectedEditor, editorContent).identical(), is(true));
    }

    protected abstract Class<?> getExtensionUnderTest();

    protected abstract String getExpectedContentFileName();

}
