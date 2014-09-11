/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.deployer;

import java.io.File;

import org.apache.felix.fileinstall.ArtifactInstaller;

/**
 *
 */
public class MabArtifactInstaller implements ArtifactInstaller
{

    @Override
    public void install(File file) throws Exception
    {

    }

    @Override
    public void update(File file) throws Exception
    {

    }

    @Override
    public void uninstall(File file) throws Exception
    {

    }

    @Override
    public boolean canHandle(File file)
    {
        //boolean isMab = false;
        //
        //if (file.getName().toLowerCase().endsWith(".mab"))
        //{
        //    ZipFile zipFile = null;
        //    try
        //    {
        //        zipFile = new ZipFile(file);
        //        isMab = zipFile.getEntry("mule-config.xml") != null;
        //    }
        //    catch (IOException e)
        //    {
        //        //TODO(pablo.kraan): OSGi - log error
        //    }
        //}
        //
        //return isMab;
        return false;
    }
}
