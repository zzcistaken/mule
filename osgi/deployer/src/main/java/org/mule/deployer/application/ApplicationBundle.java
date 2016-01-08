/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer.application;

import org.mule.config.i18n.MessageFactory;
import org.mule.deployer.DeploymentStartException;
import org.mule.deployer.DeploymentStopException;
import org.mule.deployer.InstallException;
import org.mule.deployer.MuleFoldersUtil;
import org.mule.deployer.artifact.ArtifactBundle;
import org.mule.deployer.descriptor.ApplicationDescriptor;

import java.io.File;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class ApplicationBundle implements ArtifactBundle
{

    private final BundleContext bundleContext;
    private final ApplicationDescriptor descriptor;
    private Bundle bundle;

    public ApplicationBundle(BundleContext bundleContext, ApplicationDescriptor descriptor)
    {
        this.bundleContext = bundleContext;
        this.descriptor = descriptor;
    }

    @Override
    public String getArtifactName()
    {
        return descriptor.getAppName();
    }

    @Override
    public void install() throws InstallException
    {
        File explodedAppFolder = MuleFoldersUtil.getAppFolder(descriptor.getAppName());
        try
        {
            bundle = bundleContext.installBundle("reference:" + explodedAppFolder.toURI().toString());
        }
        catch (BundleException e)
        {
            throw new InstallException(MessageFactory.createStaticMessage("Unable to install application bundle"), e);
        }
    }

    @Override
    public void start() throws DeploymentStartException
    {
        try
        {
            bundle.start();
        }
        catch (BundleException e)
        {
            throw new DeploymentStartException(MessageFactory.createStaticMessage("Unable to start application bundle"), e);
        }
    }

    @Override
    public void stop() throws DeploymentStopException
    {
        try
        {
            bundle.stop();
        }
        catch (BundleException e)
        {
            throw new DeploymentStopException(MessageFactory.createStaticMessage("Unable to stop application bundle"), e);
        }
    }

    @Override
    public void dispose()
    {
        try
        {
            bundle.uninstall();
        }
        catch (BundleException e)
        {
            //TODO(pablo.kraan): OSGi - log error
        }
    }

    @Override
    public File[] getResourceFiles()
    {
        return descriptor.getConfigResourcesFile();
    }
}
