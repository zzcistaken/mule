/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.deployer;

import java.util.Hashtable;

import org.apache.felix.fileinstall.ArtifactListener;
import org.apache.felix.fileinstall.ArtifactUrlTransformer;
import org.eclipse.equinox.internal.region.StandardRegionDigraph;
import org.eclipse.equinox.region.Region;
import org.eclipse.equinox.region.RegionDigraph;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
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

}
