/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.deployer;

import java.io.File;
import java.net.URL;

import org.apache.felix.fileinstall.ArtifactUrlTransformer;

/**
 *
 */
public class MabDeploymentListener implements ArtifactUrlTransformer
{

    @Override
    public URL transform(URL artifact) throws Exception
    {
        try {
            return new URL("mab", null, artifact.toString());
        } catch (Exception e) {
            //TODO(pablo.kraan): OSGi - add logger
            //logger.error("Unable to build blueprint application bundle", e);
            return null;
        }
    }

    @Override
    public boolean canHandle(File file)
    {
        boolean isMab = false;

        if (file.getName().toLowerCase().endsWith(".mab"))
        {
            //ZipFile zipFile = null;
            //try
            //{
            //    zipFile = new ZipFile(file);
            //    isMab = zipFile.getEntry("mule-config.xml") != null;
            //}
            //catch (IOException e)
            //{
            //    //TODO(pablo.kraan): OSGi - log error
            //}
            return true;
        }

        return isMab;
    }
}
