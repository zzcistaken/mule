/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 */
public class ApplicationZipBuilder
{

    public static class AppResource
    {
        final String file;
        final String alias;

        public AppResource(String file)
        {
            this.file = file;
            this.alias = null;
        }

        public AppResource(String file, String alias)
        {
            this.file = file;
            this.alias = alias;
        }
    }

    public static File compress(String zipFileName, AppResource[] resources) throws IOException
    {
        // out put file
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));

        try
        {
            for (AppResource appResource : resources)
            {

                FileInputStream in = new FileInputStream(ApplicationZipBuilder.class.getClassLoader().getResource(appResource.file).getFile());
                try
                {
                    // name the file inside the zip  file
                    out.putNextEntry(new ZipEntry(appResource.alias == null ? appResource.file : appResource.alias));

                    // buffer size
                    byte[] b = new byte[1024];
                    int count;

                    while ((count = in.read(b)) > 0) {
                        System.out.println();
                        out.write(b, 0, count);
                    }
                }
                finally
                {
                    in.close();
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("ERROR: " + e.getMessage());
        }
        finally
        {
            out.close();
        }

        return new File(zipFileName);
    }
}
