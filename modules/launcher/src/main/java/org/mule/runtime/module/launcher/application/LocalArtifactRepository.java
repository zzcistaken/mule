/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.launcher.application;

import static org.mule.runtime.module.launcher.MuleFoldersUtil.getRepository;
import org.mule.runtime.module.launcher.descriptor.ArtifactDependency;

import java.io.File;

public class LocalArtifactRepository implements ArtifactRepository
{

    @Override
    public File getArtifactDependencyFile(ArtifactDependency dependency)
    {
        final File artifactFile = getArtifactFile(getRepository(), dependency);

        if (!artifactFile.exists())
        {
            throw new IllegalStateException("Unable to find artifact dependency: " + dependency);
        }

        return artifactFile;
    }

    private File getArtifactFile(File pluginsDir, ArtifactDependency dependency)
    {
        final File groupFolder = new File(pluginsDir, dependency.getGroup().replace(".", File.separator));
        final File artifactFolder = new File(groupFolder, dependency.getArtifact());
        final File versionFolder = new File(artifactFolder, dependency.getVersion());
        return new File(versionFolder, dependency.getArtifact() + "-" + dependency.getVersion() + ".zip");
    }
}
