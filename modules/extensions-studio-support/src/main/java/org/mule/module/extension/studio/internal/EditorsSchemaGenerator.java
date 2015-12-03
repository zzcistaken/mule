/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.internal;

import org.mule.module.extension.studio.model.Namespace;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class EditorsSchemaGenerator
{

    public void saveSchemaToFile(final File baseDir) throws JAXBException, IOException {
        class MySchemaOutputResolver extends SchemaOutputResolver {
            public Result createOutput( String namespaceUri, String suggestedFileName ) throws IOException {
                return new StreamResult(new File(baseDir,suggestedFileName));
            }
        }

        JAXBContext context = JAXBContext.newInstance(Namespace.class);
        context.generateSchema(new MySchemaOutputResolver());
    }
}
