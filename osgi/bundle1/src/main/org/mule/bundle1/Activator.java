/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.bundle1;

import org.mule.config.MuleManifest;
import org.mule.osgi.app.MuleApplicationActivator;

import org.osgi.framework.BundleContext;

public class Activator extends MuleApplicationActivator
{

    @Override
    public void start(BundleContext context) throws Exception
    {
        System.out.println("MONCHO Starting bundle1....");
        System.out.println("MONCHO running on: " + MuleManifest.getProductName());
        super.start(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        super.stop(context);

    }
}
