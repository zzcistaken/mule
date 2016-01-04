/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.container;

import java.util.Collection;
import java.util.Collections;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.FrameworkWiring;

public class ContainerStartupListener implements StartupListener
{

    public static final boolean SHOW_BUNDLE_STATUSES = isShowBundleStatuses();
    public static final boolean SHOW_BUNDLE_DEPENDENCIES = isShowBundleDependencies();
    private static boolean isShowBundleStatuses()
    {
        String value = System.getProperty("mule.osgi.showBundleStatuses", "false");

        return Boolean.valueOf(value);
    }

    private static boolean isShowBundleDependencies()
    {
        String value = System.getProperty("mule.osgi.showBundleDependencies", "false");

        return Boolean.valueOf(value);
    }

    private final Framework framework;

    private final long startTime;

    public ContainerStartupListener(Framework framework)
    {
        this.framework = framework;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void started(boolean error)
    {
        if (SHOW_BUNDLE_STATUSES)
        {
            showBundleStatuses(framework.getBundleContext());
        }

        if (SHOW_BUNDLE_DEPENDENCIES)
        {
            showDependencies(framework.getBundleContext());
        }

        if (error)
        {
            String message = "Error starting Mule. Stopping container...";
            System.out.println(message);
            try
            {
                framework.stop();
            }
            catch (BundleException e)
            {
                System.err.println("Error stopping container: " + e);
            }
        }
        else
        {
            long startTimeSeconds = (System.currentTimeMillis() - this.startTime) / 1000;
            String message = "Mule started in " + startTimeSeconds + "s";
            System.out.println(message);
        }
    }

    private static void showDependencies(BundleContext context)
    {
        System.out.println("\nBUNDLE DEPENDENCIES:");
        FrameworkWiring frameworkWiring = context.getBundle().adapt(FrameworkWiring.class);
        for (Bundle bundle : frameworkWiring.getBundle().getBundleContext().getBundles())
        {
            System.out.println("Dependency closure for bundle: " + bundle.getSymbolicName());
            Collection<Bundle> dependencyClosure = frameworkWiring.getDependencyClosure(Collections.singleton(bundle));
            for (Bundle dependency : dependencyClosure)
            {
                System.out.println("Bundle: " + dependency.getSymbolicName());
            }
        }
    }

    private static void showBundleStatuses(BundleContext context)
    {
        System.out.println("\nBUNDLE STATUS:");

        for (Bundle bundle : context.getBundles())
        {
            System.out.println("Bundle " + bundle.getSymbolicName() + " is in state: " + getBundleState(bundle.getState()));
        }
    }

    private static String getBundleState(int state)
    {
        switch (state)
        {
            case Bundle.INSTALLED:
                return "INSTALLED";
            case Bundle.RESOLVED:
                return "RESOLVED";
            case Bundle.ACTIVE:
                return "ACTIVE";
            case Bundle.UNINSTALLED:
                return "UNINSTALLED";
            case Bundle.STARTING:
                return "STARTING";
            case Bundle.STOPPING:
                return "STOPPING";
            default:
                throw new IllegalStateException("Unknown bundle state: " + state);
        }
    }
}
