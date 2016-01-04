/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.container;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.startlevel.FrameworkStartLevel;

public class FrameworkStartupListener implements FrameworkListener, SynchronousBundleListener
{

    private final BundleContext context;
    private final StartupListener startupListener;

    FrameworkStartupListener(BundleContext context, StartupListener startupListener)
    {
        this.context = context;
        this.startupListener = startupListener;
        context.addBundleListener(this);
        context.addFrameworkListener(this);
    }

    public StartupStats getBundleStats()
    {
        Bundle[] bundles = context.getBundles();
        int numActive = 0;
        int numBundles = bundles.length;
        for (Bundle bundle : bundles)
        {
            if (bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null)
            {
                numBundles--;
            }
            else if (bundle.getState() == Bundle.ACTIVE)
            {
                numActive++;
            }
        }
        StartupStats stats = new StartupStats();
        stats.numActive = numActive;
        stats.numTotal = numBundles;
        return stats;
    }

    public synchronized void bundleChanged(BundleEvent bundleEvent)
    {
        getBundleStats();
    }

    public synchronized void frameworkEvent(FrameworkEvent frameworkEvent)
    {
        if (frameworkEvent.getType() == FrameworkEvent.STARTLEVEL_CHANGED)
        {
            //TODO(pablo.kraan): OSGi - need a system property for this value
            //int defStartLevel = Integer.parseInt(System.getProperty(Constants.FRAMEWORK_BEGINNING_STARTLEVEL));
            int defStartLevel = 0;
            int startLevel = context.getBundle(0).adapt(FrameworkStartLevel.class).getStartLevel();
            if (startLevel >= defStartLevel)
            {
                context.removeBundleListener(this);
                context.removeFrameworkListener(this);

                StartupStats stats = getBundleStats();
                boolean startError = stats.numActive < stats.numTotal;
                startupListener.started(startError);
            }
        }
    }

    public class StartupStats
    {

        int numActive;
        int numTotal;
    }
}
