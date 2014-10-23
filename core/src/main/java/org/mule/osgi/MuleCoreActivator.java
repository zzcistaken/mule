/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 */
public class MuleCoreActivator implements BundleActivator
{
    //TODO(pablo.kraan): OSGi - accesing bundleContext is not the right way to do this. Used just for the prototype
    public static BundleContext bundleContext;

    private TransportActivator transportActivator = new TransportActivator();
    @Override
    public void start(BundleContext context) throws Exception
    {
        bundleContext = context;

        transportActivator.start(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        transportActivator.stop(context);
        bundleContext = null;
    }
}
