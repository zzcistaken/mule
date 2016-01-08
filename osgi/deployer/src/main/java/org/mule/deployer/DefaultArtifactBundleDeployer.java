/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.deployer;

import org.mule.config.i18n.MessageFactory;
import org.mule.deployer.artifact.ArtifactBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultArtifactBundleDeployer<T extends ArtifactBundle> implements ArtifactBundleDeployer
{

    protected transient final Log logger = LogFactory.getLog(getClass());

    public void deploy(ArtifactBundle artifactBundle)
    {
        try
        {
            artifactBundle.install();

            artifactBundle.start();
        }
        catch (Throwable t)
        {
            artifactBundle.dispose();

            if (t instanceof DeploymentException)
            {
                throw ((DeploymentException) t);
            }

            final String msg = String.format("Failed to deploy artifact [%s]", artifactBundle.getArtifactName());
            throw new DeploymentException(MessageFactory.createStaticMessage(msg), t);
        }
    }

    public void undeploy(ArtifactBundle artifactBundle)
    {
        try
        {
            tryToStopArtifact(artifactBundle);
            tryToDisposeArtifact(artifactBundle);
        }
        catch (Throwable t)
        {
            if (t instanceof DeploymentException)
            {
                throw ((DeploymentException) t);
            }

            final String msg = String.format("Failed to undeployArtifact artifact [%s]", artifactBundle.getArtifactName());
            throw new DeploymentException(MessageFactory.createStaticMessage(msg), t);
        }
    }

    private void tryToStopArtifact(ArtifactBundle artifactBundle)
    {

        try
        {
            artifactBundle.stop();
        }
        catch (Throwable t)
        {
            logger.error(String.format("Unable to cleanly stop artifact '%s'. Restart Mule if you get errors redeploying this artifact", artifactBundle.getArtifactName()), t);
        }
    }

    private void tryToDisposeArtifact(ArtifactBundle artifactBundle)
    {
        try
        {
            artifactBundle.dispose();
        }
        catch (Throwable t)
        {
            logger.error(String.format("Unable to cleanly dispose artifact '%s'. Restart Mule if you get errors redeploying this artifact", artifactBundle.getArtifactName()), t);
        }
    }

}
