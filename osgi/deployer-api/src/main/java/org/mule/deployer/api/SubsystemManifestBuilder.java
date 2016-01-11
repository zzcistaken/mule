/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer.api;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class SubsystemManifestBuilder
{

    public File build(File subSystemFolder, String name) throws IOException
    {
        File metaInfFolder = new File(subSystemFolder, "OSGI-INF");
        if (!metaInfFolder.exists())
        {
            if (!metaInfFolder.mkdirs())
            {
                throw new IllegalStateException("Unable to create folder: " + metaInfFolder.getAbsolutePath());
            }
        }
        ManifestBuilder manifestBuilder = new ManifestBuilder(metaInfFolder);
        manifestBuilder.addHeader("Subsystem-SymbolicName", name);
        manifestBuilder.addHeader("Subsystem-Version", "1.0.0");
        //TODO(pablo.kraan): OSGi - tis must be an application, but there is a problem as XSD schemas are not found
        //manifestBuilder.addHeader("Subsystem-Type", "osgi.subsystem.application;provision-policy:= acceptDependencies");
        manifestBuilder.addHeader("Subsystem-Type", "osgi.subsystem.feature");
        manifestBuilder.addHeader("Import-Package", "org.osgi.framework;resolution:=mandatory;version=\"1.3.0\"");

        File manifestFile = manifestBuilder.build();
        if (manifestFile == null || !manifestFile.exists())
        {
            throw new IllegalStateException("Unable to create bundle manifest: " + manifestFile.getAbsolutePath());
        }

        return manifestFile;
    }

}
