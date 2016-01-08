/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer;

import org.mule.deployer.api.DeploymentService;
import org.mule.deployer.extension.DefaultMuleCoreExtensionManager;
import org.mule.deployer.extension.MuleCoreExtensionManager;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.startlevel.FrameworkStartLevel;

public class DeployerActivator implements BundleActivator
{

    private ServiceRegistration<DeploymentService> registeredDeploymentService;
    private DeploymentService deploymentService;

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        //TODO(pablo.kraan): OSGi - need a feature for the deployer in order to include it when runnging the container and not when runnignt he functinal tests
        bundleContext.addFrameworkListener(new FrameworkListener()
        {

            private DeploymentDirectoryWatcher deploymentDirectoryWatcher;

            @Override
            public void frameworkEvent(FrameworkEvent event)
            {
                if (event.getType() == FrameworkEvent.STARTLEVEL_CHANGED)
                {
                    //TODO(pablo.kraan): OSGi - need a system property for this value
                    //int defStartLevel = Integer.parseInt(System.getProperty(Constants.FRAMEWORK_BEGINNING_STARTLEVEL));
                    int defStartLevel = 0;
                    int startLevel = bundleContext.getBundle(0).adapt(FrameworkStartLevel.class).getStartLevel();
                    if (startLevel >= defStartLevel)
                    {
                        //ArtifactDeployer<Application> applicationMuleDeployer = new DefaultArtifactDeployer<>();
                        //
                        //DefaultApplicationFactory applicationFactory = new DefaultApplicationFactory();
                        //
                        //DefaultArchiveDeployer<Application> applicationDeployer = new DefaultArchiveDeployer<>(applicationMuleDeployer, applicationFactory, applications, deploymentLock, NOP_ARTIFACT_DEPLOYMENT_TEMPLATE);
                        //
                        //deploymentDirectoryWatcher = new DeploymentDirectoryWatcher(applicationDeployer, new ObservableList<Application>(), new DebuggableReentrantLock());
                        //deploymentDirectoryWatcher.start();
                        final CompositeDeploymentListener deploymentListener = new CompositeDeploymentListener();
                        OsgiDeploymentListenerManager.create(bundleContext, deploymentListener);
                        deploymentService = new MuleDeploymentService(bundleContext, deploymentListener);

                        Dictionary<String, String> serviceProperties = new Hashtable<>();
                        registeredDeploymentService = bundleContext.registerService(DeploymentService.class, deploymentService, serviceProperties);

                        MuleCoreExtensionManager coreExtensionManager = new DefaultMuleCoreExtensionManager(bundleContext);
                        try
                        {
                            coreExtensionManager.initialise();
                            coreExtensionManager.start();
                        }
                        catch (Exception e)
                        {
                            throw new IllegalStateException("Unable to start core extension manager", e);
                        }

                        deploymentService.start();
                    }
                }
            }
        });
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        if (registeredDeploymentService != null)
        {
            registeredDeploymentService.unregister();
        }

        if (deploymentService != null)
        {
            deploymentService.stop();
        }
    }
}
