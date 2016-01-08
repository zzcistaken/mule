/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer;

import org.mule.deployer.api.DeploymentListener;
import org.mule.deployer.api.DeploymentListenerManager;
import org.mule.osgi.support.OsgiServiceWrapper;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class OsgiDeploymentListenerManager extends OsgiServiceWrapper
{

    private final DeploymentListenerManager deploymentListenerManager;

    private OsgiDeploymentListenerManager(BundleContext bundleContext, DeploymentListenerManager deploymentListenerManager)
    {
        super(bundleContext);
        this.deploymentListenerManager = deploymentListenerManager;
    }

    @Override
    protected void doRegisterService(ServiceReference serviceReference)
    {
        final DeploymentListener deploymentListener = (DeploymentListener) bundleContext.getService(serviceReference);
        deploymentListenerManager.addDeploymentListener(deploymentListener);
    }

    @Override
    protected void doUnregisterService(ServiceReference serviceReference)
    {
        DeploymentListener deploymentListener = (DeploymentListener) bundleContext.getService(serviceReference);
        deploymentListenerManager.removeDeploymentListener(deploymentListener);
    }

    public static OsgiDeploymentListenerManager create(BundleContext bundleContext, DeploymentListenerManager deploymentListenerManager)
    {

        final OsgiDeploymentListenerManager listener = new OsgiDeploymentListenerManager(bundleContext, deploymentListenerManager);

        registerListener(bundleContext, listener, DeploymentListener.class);

        return listener;
    }
}