/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ManifestBuilder
{

    private final Map<String, String> headers = new HashMap<>();
    private final File parentFolder;

    public ManifestBuilder(File parentFolder)
    {
        this.parentFolder = parentFolder;
    }

    public void addHeader(String name, String value)
    {
        headers.put(name, value);
    }

    public File build() throws IOException
    {
        File manifest = new File(parentFolder, "SUBSYSTEM.MF");

        FileWriter fileWriter = new FileWriter(manifest);

        for (String key : headers.keySet())
        {
            fileWriter.append(key + ": " + headers.get(key) + "\n");
        }

        fileWriter.flush();

        fileWriter.close();

        return manifest;
    }
}
