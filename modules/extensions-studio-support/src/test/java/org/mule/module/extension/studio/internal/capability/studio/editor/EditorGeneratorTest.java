/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.internal.capability.studio.editor;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mule.api.registry.ServiceRegistry;
import org.mule.extension.api.introspection.ExtensionFactory;
import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.extension.api.introspection.declaration.fluent.Descriptor;
import org.mule.extension.api.introspection.declaration.spi.ModelEnricher;
import org.mule.module.extension.HeisenbergExtension;
import org.mule.module.extension.basic.BasicExtension;
import org.mule.module.extension.firstextension.FirstExtension;
import org.mule.module.extension.internal.DefaultDescribingContext;
import org.mule.module.extension.internal.capability.xml.XmlModelEnricher;
import org.mule.module.extension.internal.introspection.AnnotationsBasedDescriber;
import org.mule.module.extension.internal.introspection.DefaultExtensionFactory;
import org.mule.module.extension.multiconfig.MultiConfigExtension;
import org.mule.module.extension.multiprovider.MultiProviderExtension;
import org.mule.module.extension.studio.model.Namespace;
import org.mule.registry.SpiServiceRegistry;
import org.mule.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;

/**
 * Created by pablocabrera on 12/1/15.
 */
@RunWith(value = Parameterized.class)
public class EditorGeneratorTest
{

    private ExtensionFactory extensionFactory;
    private StudioEditorGenerator generator;

    private String expectedContentFileName;
    private Class<?> extensionUnderTest;

    public EditorGeneratorTest(String expectedContentFileName, Class<?> extensionUnderTest)
    {
        super();
        this.expectedContentFileName = expectedContentFileName;
        this.extensionUnderTest = extensionUnderTest;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data()
    {
        Object[][] data = {{"basic-editor.xml", BasicExtension.class},
                {"heisenberg-editor.xml", HeisenbergExtension.class},
                {"multi-config-editor.xml", MultiConfigExtension.class},
                {"first-editor.xml", FirstExtension.class},
                {"multi-provider-editor.xml", MultiProviderExtension.class}
        };
        return Arrays.asList(data);
    }

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
        Namespace editor = generator.build();
        String actualEditor = "";
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(Namespace.class);
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(editor, outputStream);
            actualEditor = outputStream.toString();
        }
        XMLUnit.setIgnoreWhitespace(true);

        String expectedEditor = IOUtils.getResourceAsString(getExpectedContentFileName(), getClass());

        XMLUnit.setNormalizeWhitespace(Boolean.TRUE);
        XMLUnit.setIgnoreWhitespace(Boolean.TRUE);
        XMLUnit.setIgnoreComments(Boolean.TRUE);

        Diff diff = new Diff(expectedEditor, actualEditor);
        if (!(diff.similar() && diff.identical()))
        {

            DetailedDiff detDiff = new DetailedDiff(diff);
            List differences = detDiff.getAllDifferences();
            StringBuilder diffLines = new StringBuilder();
            for (Object object : differences)
            {
                Difference difference = (Difference) object;
                diffLines.append(difference.toString() + '\n');
            }


            assertEquals("The Output of the template manager was not the expected:", expectedEditor, actualEditor);

            fail("Files didn't match");
        }
    }

    protected Class<?> getExtensionUnderTest()
    {
        return extensionUnderTest;
    }

    protected String getExpectedContentFileName()
    {
        return expectedContentFileName;
    }

}
