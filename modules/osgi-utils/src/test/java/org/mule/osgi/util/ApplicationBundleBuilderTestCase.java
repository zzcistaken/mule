/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.util;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

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

    //@Test
    //public void buildsAppWithStandardJar1() throws Exception
    //{
    //
    //
    //    InputStream bundle = BndUtils.createBundle(getClass().getClassLoader().getResourceAsStream("echo-test.jar"), new Properties(), "Moncho");
    //    JarInputStream jout = new JarInputStream( bundle );
    //    Manifest man = jout.getManifest();
    //    System.out.println("MANIFEST: " + man);
    //}
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
