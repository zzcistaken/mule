/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer;

import org.mule.util.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class ApplicationManifestBuilder
{

    public File build(File artifactDir) throws IOException
    {
        File metaInfFolder = new File(artifactDir, "META-INF");
        if (!metaInfFolder.exists())
        {
            if (!metaInfFolder.mkdirs())
            {
                throw new IllegalStateException("Unable to create folder: " + metaInfFolder.getAbsolutePath());
            }
        }

        String appName = FilenameUtils.getBaseName(artifactDir.getAbsolutePath());

        ManifestBuilder manifestBuilder = new ManifestBuilder(metaInfFolder);
        manifestBuilder.addHeader("Manifest-Version", "1.0");
        manifestBuilder.addHeader("Bundle-Description", "Bundle manifest automatically created for mule application " + appName);
        manifestBuilder.addHeader("Bundle-ManifestVersion", "2");
        manifestBuilder.addHeader("Bundle-Name", appName);
        manifestBuilder.addHeader("Bundle-SymbolicName", appName);
        manifestBuilder.addHeader("Bundle-Version", "0.0.0");
        manifestBuilder.addHeader("Created-By", this.getClass().getName());
        //TODO(pablo.kraan): OSGi - looks like the DynamicImport-Package directive is ignored on subsystems
        manifestBuilder.addHeader("DynamicImport-Package", "*");
        manifestBuilder.addHeader("Import-Package", "org.osgi.framework;resolution:=mandatory;version=\"[1.7\n" +
                                                           " .0,2.0.0)\",org.mule.config;resolution:=mandatory;version=\"[4.0.0,5.0.\n" +
                                                           " 0)\",org.mule.osgi.app;resolution:=mandatory;version=\"[4.0.0,5.0.0)\"");
        manifestBuilder.addHeader("Bundle-Activator", "org.mule.osgi.app.MuleApplicationActivator");
        File manifestFile = manifestBuilder.build();
        if (manifestFile == null || !manifestFile.exists())
        {
            throw new IllegalStateException("Unable to create bundle manifest: " + manifestFile.getAbsolutePath());
        }

        return manifestFile;
    }

}
