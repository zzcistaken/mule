/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer.api;

import org.mule.config.i18n.MessageFactory;
import org.mule.deployer.api.descriptor.ApplicationDescriptor;
import org.mule.util.FileUtils;

import java.io.File;
import java.io.IOException;

import org.osgi.framework.BundleContext;
import org.osgi.service.subsystem.Subsystem;

public class ApplicationBundle implements ArtifactBundle
{

    private final BundleContext bundleContext;
    private final ApplicationDescriptor descriptor;
    private final Subsystem rootSubsystem;
    private Subsystem appSubsystem;

    public ApplicationBundle(BundleContext bundleContext, ApplicationDescriptor descriptor, Subsystem rootSubsystem)
    {
        this.bundleContext = bundleContext;
        this.descriptor = descriptor;
        this.rootSubsystem = rootSubsystem;
    }

    @Override
    public String getArtifactName()
    {
        return descriptor.getAppName();
    }

    @Override
    public void install() throws InstallException
    {
        //TODO(pablo.kraan): OSGi - this must go in the deployer module as a DefaultApplicationBundle
        try
        {
            File sourceAppFolder = MuleFoldersUtil.getAppFolder(descriptor.getAppName());

            //TODO(pablo.kraan): OSGi - can't make region to deploy a reference file (to deploy exploded), so create a file from it
            File subSystemTempFolder = File.createTempFile("subSystemBundle", "tmp");
            subSystemTempFolder.delete();
            subSystemTempFolder.mkdir();
            new SubsystemManifestBuilder().build(subSystemTempFolder, descriptor.getAppName() + "-subsystem");

            File appBundleFile = compressAppBundle(sourceAppFolder, subSystemTempFolder);
            File subSystemBundleFile = compressSubsystemBundle(subSystemTempFolder);

            appSubsystem = rootSubsystem.install(subSystemBundleFile.toURI().toString());
        }
        catch (Exception e)
        {
            throw new InstallException(MessageFactory.createStaticMessage("Unable to install application bundle"), e);
        }
    }

    private File compressSubsystemBundle(File sourceFolder) throws IOException
    {
        //TODO(pablo.kraan): OSGi - refactor this code
        File tempBundle = File.createTempFile("subSystemBundle", "tmp");

        //TODO(pablo.kraan): OSGi - use a temp folder here
        FileCompressor.zip(sourceFolder, tempBundle);
        if (!tempBundle.exists())
        {
            throw new IllegalStateException("Unable to create compressed bundle");
        }

        File compressedBundle = new File(sourceFolder, descriptor.getAppName() + "-subsystem" + ".esa");
        tempBundle.renameTo(compressedBundle);
        return compressedBundle;
    }

    private File compressAppBundle(File sourceAppFolder, File subSystemTempFolder) throws IOException
    {
        File tempBundle = File.createTempFile("appBundle", "tmp");

        //TODO(pablo.kraan): OSGi - use a temp folder here
        FileCompressor.zip(sourceAppFolder, tempBundle);
        if (!tempBundle.exists())
        {
            throw new IllegalStateException("Unable to create compressed bundle");
        }

        File appBundleFile = new File(subSystemTempFolder, descriptor.getAppName() + ".jar");
        tempBundle.renameTo(appBundleFile);
        return appBundleFile;
    }

    @Override
    public void start() throws DeploymentStartException
    {
        try
        {
            appSubsystem.start();
        }
        catch (Exception e)
        {
            throw new DeploymentStartException(MessageFactory.createStaticMessage("Unable to start application bundle"), e);
        }
    }

    @Override
    public void stop() throws DeploymentStopException
    {
        if (appSubsystem != null)
        {
            try
            {
                appSubsystem.stop();
            }
            catch (Exception e)
            {
                throw new DeploymentStopException(MessageFactory.createStaticMessage("Unable to stop application bundle"), e);
            }
        }
    }

    @Override
    public void dispose()
    {
        if (appSubsystem != null)
        {
            try
            {
                appSubsystem.uninstall();
            }
            catch (Exception e)
            {
                //TODO(pablo.kraan): OSGi - log error
            }
        }
    }

    @Override
    public File[] getResourceFiles()
    {
        return descriptor.getConfigResourcesFile();
    }

}
