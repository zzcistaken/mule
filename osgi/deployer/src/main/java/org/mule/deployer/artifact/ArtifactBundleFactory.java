/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer.artifact;

import org.mule.deployer.api.ArtifactBundle;

import java.io.File;
import java.io.IOException;

public interface ArtifactBundleFactory<T extends ArtifactBundle>
{
    /**
     * Creates an Artifact bundle
     *
     * @param artifactName artifact identifier
     * @return the newly created Artifact
     */
    T create(String artifactName) throws IOException;

    /**
     * @return the directory of the Artifact. Usually this directory contains the Artifact resources
     */
    File getArtifactDir();

}
