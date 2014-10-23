/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.deployer;

import org.mule.osgi.util.ApplicationBundleBuilder;
import org.mule.util.FileUtils;
import org.mule.util.FilenameUtils;
import org.mule.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.eclipse.equinox.internal.region.StandardRegionDigraph;
import org.eclipse.equinox.region.Region;
import org.eclipse.equinox.region.RegionFilter;
import org.eclipse.equinox.region.RegionFilterBuilder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;

/**
 *
 */
public class Deployer implements Runnable
{

    private final BundleContext context;
    private final StandardRegionDigraph digraph;

    public Deployer(BundleContext context, StandardRegionDigraph digraph)
    {
        this.context = context;
        this.digraph = digraph;
    }

    @Override
    public void run()
    {
        //ServiceReference<RepositoryAdmin> serviceReference = context.getServiceReference(RepositoryAdmin.class);
        //RepositoryAdmin repositoryAdmin = context.getService(serviceReference);
        System.out.println("STARTING APPLICATION DEPLOYMENT....");
        //TODO(pablo.kraan): OSGi - move this property into a separate file
        final String BASE_FOLDER = context.getProperty("mule.server.baseFolder");

        File appsFolder = new File(BASE_FOLDER + "/apps");

        String property = context.getProperty("mule.apps");
        if (StringUtils.isEmpty(property))
        {
            throw new IllegalArgumentException("No application configured. Use 'mule.apps' system property");
        }

        try
        {
            for (String appName : property.split(","))
            {
                deployExploded(appsFolder, appName.trim());
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
    }

    protected void deployExploded(File appsFolder, String appName) throws IOException, BundleException
    {
        //TODO(pablo.kraan): OSGi - throw better exception instead fo IOException and BundleException
        Region parentRegion = digraph.getRegion("mule");
        Region appRegion = digraph.createRegion(appName);
        appRegion.connectRegion(parentRegion, new MuleRegionFilter(appName));

        // Every added bundle since here will have the app as a parent
        digraph.setDefaultRegion(appRegion);
        try
        {
            Bundle bundle = null;
            File explodedAppFolder = new File(appsFolder, appName);
            try
            {
                bundle = context.installBundle("reference:" + explodedAppFolder.toURI().toString());
            }
            catch (BundleException e)
            {
                System.err.println("Error installing application bundle: " + e.getMessage());
                e.printStackTrace();
            }

            List<Bundle> libBundles = installAppLibs(appRegion, explodedAppFolder);


            List<Bundle> pluginBundles = installAppPlugins(appRegion, explodedAppFolder);

            //for (Bundle plugin : pluginBundles)
            //{
            //    plugin.start();
            //}

            try
            {
                //for (Bundle libBundle : libBundles)
                //{
                //    libBundle.start();
                //}

                bundle.start();
            }
            catch (BundleException e)
            {
                System.err.println("Error starting application bundle: " + e.getMessage());
            }
        }
        finally
        {
            digraph.setDefaultRegion(null);
        }

    }

    private List<Bundle> installAppPlugins(Region appRegion, File explodedAppFolder) throws BundleException
    {
        File appPluginsFolder = new File(explodedAppFolder, "plugins");

        List<Bundle> pluginBundles = new ArrayList<>();

        if (appPluginsFolder.exists())
        {
            String[] plugins = appPluginsFolder.list(new SuffixFileFilter(".zip"));
            if (plugins != null)
            {
                for (String plugin : plugins)
                {
                    Bundle libBundle = installPlugin(appRegion, appPluginsFolder, plugin);
                    //pluginBundles.add(libBundle);
                }
            }
        }
        //TODO(pablo.kraan): OSGi- remove this result
        return pluginBundles;
    }

    private Bundle installPlugin(Region appRegion, File appPluginsFolder, String pluginZip) throws BundleException
    {
        final String pluginName = StringUtils.removeEnd(pluginZip, ".zip");

        //digraph.setDefaultRegion(null);
        Region pluginRegion = digraph.createRegion(pluginName);
        pluginRegion.connectRegion(appRegion, new MuleRegionFilter(pluginName));
        digraph.setDefaultRegion(pluginRegion);
        //Region pluginRegion = digraph.getRegion("mule");

        //TODO(pablo.kraan): OSGi - use mule temp folder
        final File tmpDir = new File("/tmp/muleTemp", appPluginsFolder.getParentFile().getName() + "/plugins/");
        try
        {
            FileUtils.unzip(new File(appPluginsFolder, pluginZip), tmpDir);
        }
        catch (IOException e)
        {
            throw new BundleException("Error unzipping plugin", e);
        }

        File pluginDir = new File(tmpDir, pluginName);
        String[] libs = pluginDir.list(new SuffixFileFilter(".jar"));
        if (libs != null)
        {
            for (String lib : libs)
            {
                List<String> exportedPackages = new ArrayList<>();

                File libFile = new File(pluginDir, lib);
                Bundle libBundle = pluginRegion.installBundle(libFile.toURI().toString());

                //TODO(pablo.kraan): OSGi - add some plugin.properties or some MANIFEST entry to identify the bundle that must be exposed
                if (lib.contains("plugin"))
                {
                    try
                    {
                        JarFile pluginJar = new JarFile(libFile);

                        String exportedAttribute = pluginJar.getManifest().getMainAttributes().getValue(Constants.EXPORT_PACKAGE);
                        if (!StringUtils.isEmpty(exportedAttribute))
                        {
                            for (String exportedPackage : exportedAttribute.split(","))
                            {
                                String[] packageAttributes = exportedPackage.split(";");
                                exportedPackages.add(packageAttributes[0]);
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        throw new BundleException("Unable to read plugin jar", e);
                    }

                    try
                    {
                        //TODO(pablo.kraan): OSGi - need to read the manifest and use the export-package attribute to create the filter
                        //appRegion.connectRegion(pluginRegion, digraph.createRegionFilterBuilder().allow(RegionFilter.VISIBLE_PACKAGE_NAMESPACE, "(" + RegionFilter.VISIBLE_PACKAGE_NAMESPACE + "=org.echo)").build());
                        RegionFilterBuilder regionFilterBuilder = digraph.createRegionFilterBuilder();
                        for (String exportedPackage : exportedPackages)
                        {
                            regionFilterBuilder.allow(RegionFilter.VISIBLE_PACKAGE_NAMESPACE, "(" + RegionFilter.VISIBLE_PACKAGE_NAMESPACE + "=" + exportedPackage + ")");
                        }

                        appRegion.connectRegion(pluginRegion, regionFilterBuilder.build());
                    }
                    catch (InvalidSyntaxException e)
                    {
                        throw new BundleException("Error creating plugin region: " + pluginName, e);
                    }
                }
            }
        }

        //Bundle plugin = appRegion.installBundleAtLocation(pluginZip, null);

        //digraph.getDefaultRegion().addBundle(plugin);
        return null;
    }

    private List<Bundle> installAppLibs(Region appRegion, File explodedAppFolder) throws BundleException
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
                    Bundle libBundle = appRegion.installBundle(new File(appLibFolder, lib).toURI().toString());
                    libBundles.add(libBundle);
                }
            }
        }
        return libBundles;
    }

    protected void deploy(File appsFolder, String appFile) throws IOException, BundleException
    {
        Region parentRegion = digraph.getRegion("mule");

        String appName = FilenameUtils.getBaseName(appFile);
        Region app = digraph.createRegion(appName);
        //app.connectRegion(parentRegion, new MuleRegionFilter(appName));

        ApplicationBundleBuilder bundleBuilder = new ApplicationBundleBuilder();
        File bundleTempFile = bundleBuilder.build(new File(appsFolder, appFile));
        File bundleFile = new File(appsFolder, bundleTempFile.getName());
        bundleTempFile.renameTo(bundleFile);

        Bundle bundle = null;
        try
        {
            bundle = app.installBundle(bundleFile.toURI().toString());
        }
        catch (BundleException e)
        {
            System.err.println("Error installing application bundle: " + e.getMessage());
        }

        try
        {
            bundle.start();
        }
        catch (BundleException e)
        {
            System.err.println("Error starting application bundle: " + e.getMessage());
        }
    }

    private static class MuleRegionFilter implements RegionFilter
    {
        public static final boolean SHOW_REGION_FILTERING = isShowRegionFiltering();

        private static boolean isShowRegionFiltering()
        {
            String value = System.getProperty("mule.osgi.showRegionFiltering", "false");

            return Boolean.valueOf(value);
        }

        private final String region;

        private MuleRegionFilter(String region)
        {
            this.region = region;
        }

        @Override
        public boolean isAllowed(Bundle bundle)
        {
            logRegionFiltering("Region: " + region + " - isAllowed bundle: " + bundle.getSymbolicName());
            return true;
        }

        @Override
        public boolean isAllowed(BundleRevision bundleRevision)
        {
            logRegionFiltering("Region: " + region + " - isAllowed bundleRevision:: " + bundleRevision);
            return true;
        }

        @Override
        public boolean isAllowed(ServiceReference<?> serviceReference)
        {
            logRegionFiltering("Region: " + region + " - isAllowed: serviceReference: " + serviceReference);
            return true;
        }

        @Override
        public boolean isAllowed(BundleCapability bundleCapability)
        {
            logRegionFiltering("Region: " + region + " - isAllowed: bundleCapability: " + bundleCapability);
            return true;
        }

        @Override
        public boolean isAllowed(String s, Map<String, ?> stringMap)
        {
            logRegionFiltering("Region: " + region + " - isAllowed: " + s + " stringMap: " + stringMap);
            return true;
        }

        @Override
        public Map<String, Collection<String>> getSharingPolicy()
        {
            logRegionFiltering("Region: " + region + " - isAllowed: getSharingPolicy");
            return null;
        }

        private void logRegionFiltering(String message)
        {
            if (SHOW_REGION_FILTERING)
            {
                System.out.println(message);
            }
        }
    }
}
