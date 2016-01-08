/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer.api;

import java.io.File;

public interface ArtifactBundle
{

    /**
     * @return the artifact identifier
     */
    String getArtifactName();

    /**
     * Install the bundle and dependencies on the container
     */
    void install() throws InstallException;

    void start() throws DeploymentStartException;

    void stop() throws DeploymentStopException;

    void dispose();

    /**
     * @return an array with the configuration files of the artifact. Never returns null.
     *         If there's no configuration file then returns an empty array.
     */
    File[] getResourceFiles();
}
