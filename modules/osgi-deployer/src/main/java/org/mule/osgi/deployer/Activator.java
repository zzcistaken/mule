/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.deployer;

import org.mule.osgi.util.ApplicationBundleBuilder;
import org.mule.util.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.felix.fileinstall.ArtifactListener;
import org.apache.felix.fileinstall.ArtifactUrlTransformer;
import org.eclipse.equinox.internal.region.StandardRegionDigraph;
import org.eclipse.equinox.region.Region;
import org.eclipse.equinox.region.RegionDigraph;
import org.eclipse.equinox.region.RegionFilter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.service.url.URLStreamHandlerService;

/**
 *
 */
public class Activator implements BundleActivator
{

    private Thread deployerThread;

    @Override
    public void start(final BundleContext context) throws Exception
    {
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put("url.handler.protocol", "mab");
        context.registerService(
                URLStreamHandlerService.class.getName(),
                new MabURLHandler(), props);

        //Hashtable<String, Object> props = new Hashtable<String, Object>();
        //props.put("url.handler.protocol", "mab");
        //context.registerService(
        //        URLStreamHandlerService.class.getName(),
        //        new MabURLHandler(), props);

        context.registerService(new String[] {ArtifactUrlTransformer.class.getName(), ArtifactListener.class.getName()},
                                new MabDeploymentListener(), new Hashtable<String, Object>());

        //context.registerService(ResolverHookFactory.class.getName(), new ResolverHookFactory()
        //{
        //    @Override
        //    public ResolverHook begin(Collection<BundleRevision> triggers)
        //    {
        //        //TODO(pablo.kraan): oSGi - maybe it will required to pass the triggers in the resolverHook constructor
        //        return new MuleResolverHook();
        //    }
        //}, null);

        ThreadLocal<Region> threadLocal = new ThreadLocal<>();
        final StandardRegionDigraph digraph = createDigraph(context, threadLocal);
        register(context, ResolverHookFactory.class, digraph.getResolverHookFactory());
        //register(context, CollisionHook.class, digraph.getBundleCollisionHook());
        register(context, org.osgi.framework.hooks.bundle.FindHook.class, digraph.getBundleFindHook());
        register(context, org.osgi.framework.hooks.bundle.EventHook.class, digraph.getBundleEventHook());
        register(context, org.osgi.framework.hooks.service.FindHook.class, digraph.getServiceFindHook());
        register(context, org.osgi.framework.hooks.service.EventHook.class, digraph.getServiceEventHook());
        register(context, RegionDigraph.class, digraph);

        context.addFrameworkListener(new FrameworkListener()
        {
            @Override
            public void frameworkEvent(FrameworkEvent event)
            {
                if (event.getType() == FrameworkEvent.STARTED)
                {
                    deployerThread = new Thread(new Deployer(context, digraph));
                    deployerThread.start();
                }
            }
        });
    }

    private void register(BundleContext context, Class serviceClass, Object service)
    {
        context.registerService(new String[] {serviceClass.getName()}, service, new Hashtable<String, Object>());
    }

    private StandardRegionDigraph createDigraph(BundleContext bundleContext, ThreadLocal<Region> threadLocal) throws BundleException
    {
        StandardRegionDigraph digraph = new StandardRegionDigraph(bundleContext, threadLocal);
        Region root = digraph.createRegion("mule");
        for (Bundle bundle : bundleContext.getBundles())
        {
            root.addBundle(bundle);
        }

        return digraph;
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        if (deployerThread != null)
        {
            deployerThread.interrupt();
            deployerThread.join(5000);
        }

    }

    private static class Deployer implements Runnable
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
            System.out.println("STARTING APPLICATION DEPLOYMENT....");


            File appsFolder = new File("/Users/pablokraan/devel/osgiexample/apps");
            String[] appFiles = appsFolder.list(new SuffixFileFilter(".zip"));
            if (appFiles != null)
            {
                for (String appFile : appFiles)
                {
                    try
                    {
                        deploy(appsFolder, appFile);
                    }
                    catch (Exception e)
                    {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }

        protected void deploy(File appsFolder, String appFile) throws IOException, BundleException
        {
            Region parentRegion = digraph.getRegion("mule");

            String appName = FilenameUtils.getBaseName(appFile);
            Region app = digraph.createRegion(appName);
            app.connectRegion(parentRegion, new MuleRegionFilter());

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

            @Override
            public boolean isAllowed(Bundle bundle)
            {
                System.out.println("isAllowed bundle: " + bundle.getSymbolicName());
                return true;
            }

            @Override
            public boolean isAllowed(BundleRevision bundleRevision)
            {
                System.out.println("isAllowed bundleRevision:: " + bundleRevision);
                return true;
            }

            @Override
            public boolean isAllowed(ServiceReference<?> serviceReference)
            {
                System.out.println("isAllowed: serviceReference: " + serviceReference);
                return true;
            }

            @Override
            public boolean isAllowed(BundleCapability bundleCapability)
            {
                System.out.println("isAllowed: bundleCapability: " + bundleCapability);
                return true;
            }

            @Override
            public boolean isAllowed(String s, Map<String, ?> stringMap)
            {
                System.out.println("isAllowed: " + s+ " stringMap: " + stringMap);
                return true;
            }

            @Override
            public Map<String, Collection<String>> getSharingPolicy()
            {
                System.out.println("isAllowed: getSharingPolicy");
                return null;
            }
        }
    }
}
