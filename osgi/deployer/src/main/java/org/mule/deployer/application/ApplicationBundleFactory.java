/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer.application;

import org.mule.deployer.AppBloodhound;
import org.mule.deployer.DefaultAppBloodhound;
import org.mule.deployer.api.MuleFoldersUtil;
import org.mule.deployer.api.ApplicationBundle;
import org.mule.deployer.artifact.ArtifactBundleFactory;
import org.mule.deployer.api.descriptor.ApplicationDescriptor;

import java.io.File;
import java.io.IOException;

import org.eclipse.equinox.region.RegionDigraph;
import org.osgi.framework.BundleContext;

public class ApplicationBundleFactory implements ArtifactBundleFactory<ApplicationBundle>
{

    private final BundleContext bundleContext;
    private final RegionDigraph regions;

    public ApplicationBundleFactory(BundleContext bundleContext, RegionDigraph regions)
    {
        this.bundleContext = bundleContext;
        this.regions = regions;
    }

    @Override
    public ApplicationBundle create(String artifactName) throws IOException
    {
        if (artifactName.contains(" "))
        {
            throw new IllegalArgumentException("Mule application name may not contain spaces: " + artifactName);
        }

        AppBloodhound bh = new DefaultAppBloodhound();
        final ApplicationDescriptor descriptor = bh.fetch(artifactName);

        return new ApplicationBundle(bundleContext, descriptor, regions);
    }

    @Override
    public File getArtifactDir()
    {
        return MuleFoldersUtil.getAppsFolder();
    }
}
