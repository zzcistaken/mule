/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.launcher;

import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

/**
 *
 */
public class BundleResolveErrorListener implements FrameworkListener
{

    //private final List<Bundle> bundles;

    @Override
    public void frameworkEvent(FrameworkEvent frameworkEvent)
    {
         //if (frameworkEvent.getType() == FrameworkEvent.ERROR)
         //{
         //    frameworkEvent.getBundle().getBundleId()
         //}
    }

    //public void waitForEvents(List<Bundle> bundles)
    //{
    //
    //}
}
