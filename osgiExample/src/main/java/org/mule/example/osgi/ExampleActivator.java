/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.example.osgi;

import org.mule.MuleServer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 */
public class ExampleActivator implements BundleActivator
{

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        System.out.println("MONCHO starting!!!");

        try
        {
            //TODO(pablo.kraan): using a file that is outside the bundle for now
            MuleServer muleServer = new MuleServer("/Users/pablokraan/devel/osgiexample/mule-config.xml");
            muleServer.start(true, true);
        }
        catch (Throwable e)
        {
            System.out.println("MONCHO Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("MONCHO STARTED");


    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        System.out.println("MONCHO stop!!!");
    }
}
