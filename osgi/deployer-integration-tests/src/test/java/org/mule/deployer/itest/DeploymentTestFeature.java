/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer.itest;

import org.mule.osgi.feature.model.BundleInfo;
import org.mule.osgi.feature.model.Dependency;
import org.mule.osgi.feature.model.FeatureInfo;

import java.util.ArrayList;
import java.util.List;

public class DeploymentTestFeature extends FeatureInfo
{

    public DeploymentTestFeature()
    {
        super("deployment-test", createDependencies());
    }

    private static List<Dependency> createDependencies()
    {
        final List<Dependency> dependencies = new ArrayList<>();

        dependencies.add(new BundleInfo("mvn:org.mule.tests/mule-tests-unit/4.0-SNAPSHOT/jar", 75));

        dependencies.add(new BundleInfo("mvn:org.ops4j.base/ops4j-base-monitors/1.5.0/jar", 75));
        dependencies.add(new BundleInfo("mvn:org.ops4j.base/ops4j-base-lang/1.5.0/jar", 75));
        dependencies.add(new BundleInfo("mvn:org.ops4j.base/ops4j-base-io/1.5.0/jar", 75));
        dependencies.add(new BundleInfo("mvn:org.ops4j.base/ops4j-base-store/1.5.0/jar", 75));

        dependencies.add(new BundleInfo("mvn:biz.aQute.bnd/bndlib/2.4.0/jar", 75));
        dependencies.add(new BundleInfo("mvn:org.ops4j.pax.tinybundles/tinybundles/2.1.1/jar", 75));

        return dependencies;
    }
}
