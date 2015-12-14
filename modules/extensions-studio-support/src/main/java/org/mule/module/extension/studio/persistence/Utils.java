/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.persistence;

import org.mule.module.extension.studio.model.Namespace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class Utils implements INamespaceDeserializer<File>, INamespaceSerializer<File>
{

    @Override
    public void serialize(Namespace namespace, File output)
    {

        if (output == null)
        {
            throw new IllegalArgumentException("File cannot be null");
        }
        try (FileOutputStream outputStream = new FileOutputStream(output))
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(Namespace.class);
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(namespace, outputStream);
        }
        catch (JAXBException | FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Namespace deserialize(File input)
    {
        if (input == null)
        {
            throw new IllegalArgumentException("Could not adapt input to IFile");
        }

        JAXBContext jaxbContext;
        Namespace namspace = null;
        try
        {
            jaxbContext = JAXBContext.newInstance(Namespace.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            namspace = (Namespace) jaxbUnmarshaller.unmarshal(input);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return namspace;
    }

}
