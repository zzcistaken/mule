/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.util;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.ops4j.pax.swissbox.bnd.BndUtils;

public class ApplicationBundleBuilderTestCase
{

    @Test
    public void buildsAppWithConfigOnly() throws Exception
    {
        File zipFile = ApplicationZipBuilder.compress("/Users/pablokraan/devel/osgiexample/apps/simpleApp.zip", new ApplicationZipBuilder.AppResource[] {new ApplicationZipBuilder.AppResource("mule-config.xml")});


        File bundle = new ApplicationBundleBuilder().build(zipFile);

        assertThat(bundle, not(nullValue()));
        bundle.renameTo(new File("/Users/pablokraan/Downloads/tempBundle.jar"));
    }

    @Test
    @Ignore
    /**
     * Not a real test, used to generate a bundle from a standard jar
     */
    public void buildsAppWithStandardJar1() throws Exception
    {
        InputStream bundle = BndUtils.createBundle(getClass().getClassLoader().getResourceAsStream("echo-test.jar"), new Properties(), "echo-test-osgi");
        writeToFile("/Users/pablokraan/devel/osgiexample/apps/externalLib/lib/echo-test-osgi.jar", bundle);
    }

    private void writeToFile(String fileName, InputStream content)
    {
        try
        {
            OutputStream os = new FileOutputStream(fileName);

            byte[] buffer = new byte[1024];
            int bytesRead;
            //read from is to buffer
            while ((bytesRead = content.read(buffer)) != -1)
            {
                os.write(buffer, 0, bytesRead);
            }
            content.close();
            //flush OutputStream to write any buffered data to file
            os.flush();
            os.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //
    //@Test
    //public void buildsAppWithStandardJar2() throws Exception
    //{
    //    InputStream bundle = BndUtils.createBundle(getClass().getClassLoader().getResourceAsStream("uuid-3.4.0.jar"), new Properties(), "Moncho");
    //    JarInputStream jout = new JarInputStream( bundle );
    //    Manifest man = jout.getManifest();
    //    System.out.println("MANIFEST: " + man);
    //}
}
