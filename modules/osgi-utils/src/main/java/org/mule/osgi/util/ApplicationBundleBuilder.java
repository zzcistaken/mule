/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.util;

import org.mule.util.FileUtils;
import org.mule.util.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class ApplicationBundleBuilder
{

    public File build(File zipFile) throws IOException
    {

        File tempFolder = File.createTempFile("app", "tmp");

        if(!tempFolder.delete() && !tempFolder.mkdir())
        {
            throw new IllegalStateException("Unable to create temporary folder");
        }

        FileUtils.unzip(zipFile, tempFolder);


        File metaInfFolder = new File(tempFolder, "META-INF");
        if (!metaInfFolder.mkdirs())
        {
            throw new IllegalStateException("Unable to create META-INF folder");
        }

        String appName = FilenameUtils.getBaseName(zipFile.getAbsolutePath());

        ManifestBuilder manifestBuilder = new ManifestBuilder(metaInfFolder);
        manifestBuilder.addHeader("Manifest-Version", "1.0");
        manifestBuilder.addHeader("Bundle-Description", "OSGi bundle automatically created for mule application " + appName);
        manifestBuilder.addHeader("Bundle-ManifestVersion", "2");
        manifestBuilder.addHeader("Bundle-Name", appName);
        manifestBuilder.addHeader("Bundle-SymbolicName", appName);
        manifestBuilder.addHeader("Bundle-Version", "0.0.0");
        manifestBuilder.addHeader("Created-By", this.getClass().getName());
        manifestBuilder.addHeader("DynamicImport-Package", "*");
        manifestBuilder.addHeader("Bundle-Activator", "org.mule.module.springconfig.osgi.MuleApplicationActivator");
        File manifestFile = manifestBuilder.build();
        if (manifestFile == null || !manifestFile.exists())
        {
            throw new IllegalStateException("Unable to create bundle manifest");
        }

        File tempBundle = File.createTempFile("bundle", "tmp");

        //FileCompressor fileCompressor = new FileCompressor();
        //tempBundle = fileCompressor.compress(tempFolder.getAbsolutePath(), tempBundle.getAbsolutePath());
        FileCompressor.zip(tempFolder, tempBundle);
        if (!tempBundle.exists())
        {
            throw new IllegalStateException("Unable to create compressed bundle");
        }

        File bundle = new File(tempFolder, appName + ".jar");
        tempBundle.renameTo(bundle);

        return bundle;
    }

}
