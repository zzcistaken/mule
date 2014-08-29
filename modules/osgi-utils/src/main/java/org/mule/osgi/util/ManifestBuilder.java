package org.mule.osgi.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
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
        File manifest = new File(parentFolder, "MANIFEST.MF");

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
