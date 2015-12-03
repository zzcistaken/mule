/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.internal.capability.studio.editor;

import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.extension.api.introspection.property.StudioEditorModelProperty;
import org.mule.extension.api.resources.ResourcesGenerator;
import org.mule.extension.api.resources.spi.GenerableResourceContributor;
import org.mule.module.extension.studio.model.Namespace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Created by pablocabrera on 11/18/15.
 */
public class StudioEditorResourceContributor implements GenerableResourceContributor
{

    @Override
    public void contribute(ExtensionModel extensionModel, ResourcesGenerator resourcesGenerator)
    {
        StudioEditorModelProperty studioEditorModelProperty = extensionModel.getModelProperty(StudioEditorModelProperty.KEY);
        if(studioEditorModelProperty!=null){
            generateEditorFile(extensionModel,studioEditorModelProperty,resourcesGenerator);
        }
    }

    private void generateEditorFile(ExtensionModel extensionModel, StudioEditorModelProperty studioEditorModelProperty, ResourcesGenerator resourcesGenerator)
    {
        Namespace extensionEditorModel = getEditorModelFrom(extensionModel);

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(Namespace.class);
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(extensionEditorModel, outputStream);
            resourcesGenerator.get(studioEditorModelProperty.getFileName()).getContentBuilder().append(outputStream.toString());
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private Namespace getEditorModelFrom(ExtensionModel extensionModel)
    {
        StudioEditorGenerator studioEditorGenerator = StudioEditorGenerator.newStudioEditorGenerator(extensionModel);
        return studioEditorGenerator.build();
    }
}
