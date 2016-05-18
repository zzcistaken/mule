/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.launcher.descriptor;

public class ArtifactDependency
{

    private final String group;
    private final String artifact;
    private final String version;

    public ArtifactDependency(String group, String artifact, String version)
    {
        this.group = group;
        this.artifact = artifact;
        this.version = version;
    }

    public String getGroup()
    {
        return group;
    }

    public String getArtifact()
    {
        return artifact;
    }

    public String getVersion()
    {
        return version;
    }

    @Override
    public String toString()
    {
        return group + ":" + artifact + ":" + version;
    }
}
