/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.deployer;

import java.util.Collection;

import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;

/**
 *
 */
public class MuleResolverHook implements ResolverHook
{

    @Override
    public void filterResolvable(Collection<BundleRevision> candidates)
    {

    }

    @Override
    public void filterSingletonCollisions(BundleCapability singleton, Collection<BundleCapability> collisionCandidates)
    {

    }

    @Override
    public void filterMatches(BundleRequirement requirement, Collection<BundleCapability> candidates)
    {

    }

    @Override
    public void end()
    {

    }
}
