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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.eclipse.equinox.region.Region;
import org.eclipse.equinox.region.RegionDigraph;
import org.eclipse.equinox.region.RegionFilter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;

public class ApplicationBundle implements ArtifactBundle
{

    private final BundleContext bundleContext;
    private final ApplicationDescriptor descriptor;
    private final RegionDigraph regions;
    private Bundle bundle;
    private List<Bundle> appLibraries;
    private final List<String> regionBundleSymbolicNames = new LinkedList<>();

    public ApplicationBundle(BundleContext bundleContext, ApplicationDescriptor descriptor, RegionDigraph regions)
    {
        this.bundleContext = bundleContext;
        this.descriptor = descriptor;
        this.regions = regions;
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
            Region parentRegion = regions.getRegion(0);
            Region appRegion = regions.createRegion(descriptor.getAppName());
            //final RegionFilterBuilder regionFilterBuilder = regions.createRegionFilterBuilder();
            //regionFilterBuilder.allowAll("org.mule");
            //final RegionFilter regionFilter = regionFilterBuilder.build();
            appRegion.connectRegion(parentRegion, new MuleRegionFilter(descriptor.getAppName(), regionBundleSymbolicNames));
            File explodedAppFolder = MuleFoldersUtil.getAppFolder(descriptor.getAppName());

            //TODO(pablo.kraan): OSGi - can't make region to deploy a reference file (to deploy exploded), so create a file from it
            File tempBundle = File.createTempFile("bundle", "tmp");

            //FileCompressor fileCompressor = new FileCompressor();
            //tempBundle = fileCompressor.compress(tempFolder.getAbsolutePath(), tempBundle.getAbsolutePath());
            //TODO(pablo.kraan): OSGi - use a temp folder here
            FileCompressor.zip(explodedAppFolder, tempBundle);
            if (!tempBundle.exists())
            {
                throw new IllegalStateException("Unable to create compressed bundle");
            }

            File bundleFile = new File(MuleFoldersUtil.getExecutionFolder(), descriptor.getAppName()+ ".jar");
            tempBundle.renameTo(bundleFile);

            appLibraries = installPluginLibs(appRegion, explodedAppFolder);
            bundle = appRegion.installBundle(bundleFile.toURI().toString());
        }
        catch (Exception e)
        {
            throw new InstallException(MessageFactory.createStaticMessage("Unable to install application bundle"), e);
        }
        finally
        {
            //TODO(pablo.kraan): OSGi - is this still needed?
            //regions.setDefaultRegion(null);
        }}


    private List<Bundle> installPluginLibs(Region appRegion, File explodedAppFolder) throws BundleException
    {
        File appLibFolder = new File(explodedAppFolder, "lib");
        List<Bundle> libBundles = new ArrayList<>();
        if (appLibFolder.exists())
        {
            String[] libs = appLibFolder.list(new SuffixFileFilter(".jar"));
            if (libs != null)
            {
                for (String lib : libs)
                {
                    try
                    {
                        //TODO(pablo.kraan): OSGi - don't assume that all libs are bundles
                        Bundle libBundle;
                        final File bundleFile = new File(appLibFolder, lib);
                        final String bundleSymbolicName = findBundleSymbolicName(bundleFile);
                        regionBundleSymbolicNames.add(bundleSymbolicName);

                        //{
                        libBundle = appRegion.installBundle(bundleFile.toURI().toString());
                        //}
                        //else
                        //{
                        //    libBundle = appRegion.installBundle("wrap:" + bundleFile.toURI().toString());
                        //
                        //}

                        libBundles.add(libBundle);
                    }
                    catch (BundleException e)
                    {
                        throw new IllegalStateException("Unable to install bundle: " + lib, e);
                    }
                }
            }
        }
        return libBundles;
    }

    private static boolean isFragment(Bundle bundle)
    {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }

    private String findBundleSymbolicName(File bundleFile)
    {
        //TODO(pablo.kraan): OSGi - having to uncompress jars just to detect if they are bundles and get the symbolicName or wrap them
        // is kind of overkill. It would be better to have som kind of deployment descriptor so dependencies can be listed there
        // and separate bundles of plain jars. Fall back to unzipping files if there is no descriptor.
        try
        {
            File tempBundleFolder = File.createTempFile(bundleFile.getName(), "tmp");
            tempBundleFolder.delete();
            tempBundleFolder.mkdir();
            FileUtils.unzip(bundleFile, tempBundleFolder);

            final File metaInfFolder = new File(tempBundleFolder, "META-INF");
            if (metaInfFolder.exists())
            {
                final File manifestFile = new File(metaInfFolder, "MANIFEST.MF");
                if (manifestFile.exists())
                {
                    final Properties properties = new Properties();
                    properties.load(manifestFile.toURI().toURL().openStream());

                    return (String) properties.get(Constants.BUNDLE_SYMBOLICNAME);
                }
            }
        }
        catch (IOException e)
        {
            // Ignore
        }

        return null;
    }

    @Override
    public void start() throws DeploymentStartException
    {
        try
        {
            //TODO(pablo.kraan): OSGi - what happens if there are many bundles and have dependencies between them? should they be resolved in order?
            //FrameworkWiring frameworkWiring = bundleContext.getBundle(0).adapt(FrameworkWiring.class);
            //if (frameworkWiring.resolveBundles(appLibraries))
            //{

                for (Bundle libBundle : appLibraries)
                {
                    if (!isFragment(libBundle))
                    {
                        libBundle.start();
                    }
                }
            //}
            //else
            //{
            //    throw new IllegalStateException("Unable to resolve application bundles. " + frameworkWiring.getRemovalPendingBundles());
            //}

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
        if (bundle != null)
        {

            try
            {
                bundle.stop();

                for (Bundle libBundle : appLibraries)
                {
                    if (!isFragment(libBundle))
                    {
                        libBundle.stop();
                    }
                }
            }
            catch (BundleException e)
            {
                throw new DeploymentStopException(MessageFactory.createStaticMessage("Unable to stop application bundle"), e);
            }
        }
    }

    @Override
    public void dispose()
    {
        if (bundle != null)
        {
            try
            {
                bundle.uninstall();
                for (Bundle libBundle : appLibraries)
                {
                   libBundle.uninstall();
                }
            }
            catch (BundleException e)
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

    private static class MuleRegionFilter implements RegionFilter
    {
        public static final boolean SHOW_REGION_FILTERING = isShowRegionFiltering();

        private static boolean isShowRegionFiltering()
        {
            String value = System.getProperty("mule.osgi.showRegionFiltering", "false");

            return Boolean.valueOf(value);
        }

        private final String regionName;
        private final List<String> regionBundleSymbolicNames;

        private MuleRegionFilter(String regionName, List<String> regionBundleSymbolicNames)
        {
            this.regionName = regionName;
            this.regionBundleSymbolicNames = regionBundleSymbolicNames;
        }

        @Override
        public boolean isAllowed(Bundle bundle)
        {
            //TODO(pablo.kraan): OSGi - apps should not override mule's bundles
            final boolean allow = !"org.mule.osgi.mule-bundle1".equals(bundle.getSymbolicName());

            logRegionFiltering("Region: " + regionName + " - isAllowed bundle: " + bundle.getSymbolicName() + "- Allow: " + allow);
            return allow;
            //return false;
        }

        @Override
        public boolean isAllowed(BundleRevision bundleRevision)
        {
            final boolean allow = !regionBundleSymbolicNames.contains(bundleRevision.getSymbolicName());
            logRegionFiltering("Region: " + regionName + " - isAllowed bundleRevision:: " + bundleRevision + " Allow: " + allow);
            return allow;
        }

        @Override
        public boolean isAllowed(ServiceReference<?> serviceReference)
        {
            logRegionFiltering("Region: " + regionName + " - isAllowed: serviceReference: " + serviceReference);
            return true;
        }

        @Override
        public boolean isAllowed(BundleCapability bundleCapability)
        {
            final boolean allow = !regionBundleSymbolicNames.contains(bundleCapability.getResource().getSymbolicName());
            logRegionFiltering("Region: " + regionName + " - isAllowed: bundleCapability: " + bundleCapability + " Allow: " + allow);
            return allow;
        }

        @Override
        public boolean isAllowed(String s, Map<String, ?> stringMap)
        {
            logRegionFiltering("Region: " + regionName + " - isAllowed: " + s + " stringMap: " + stringMap);
            return true;
        }

        @Override
        public Map<String, Collection<String>> getSharingPolicy()
        {
            //logRegionFiltering("Region: " + regionName + " - isAllowed: getSharingPolicy");
            //return null;
            return new HashMap<>();
        }

        private void logRegionFiltering(String message)
        {
            //if (SHOW_REGION_FILTERING)
            //{
                System.out.println("MONCHO " + message);
            //}
        }
    }
}
