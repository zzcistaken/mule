/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tck.junit4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

/**
 *
 */
public class BundleContextBuilder
{
    public BundleContext build()
    {
        BundleContext bundleContext = mock(BundleContext.class);

        Bundle bundle = mock(Bundle.class);
        when(bundleContext.getBundle()).thenReturn(bundle);

        BundleWiring bundleWiring = mock(BundleWiring.class);
        when(bundle.adapt(BundleWiring.class)).thenReturn(bundleWiring);
        when(bundleWiring.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());

        return bundleContext;
    }
}
