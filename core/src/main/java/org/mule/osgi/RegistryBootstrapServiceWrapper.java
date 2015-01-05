/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.mule.config.bootstrap.BootstrapPropertiesService;
import org.mule.config.bootstrap.RegistryBootstrapService;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

/**
 *
 */
public class RegistryBootstrapServiceWrapper extends OsgiServiceWrapper
{

    private final RegistryBootstrapService registryBootstrapService;

    public RegistryBootstrapServiceWrapper(BundleContext bundleContext, RegistryBootstrapService registryBootstrapService)
    {
        super(bundleContext);
        this.registryBootstrapService = registryBootstrapService;
    }

    @Override
    protected void doRegisterService(ServiceReference serviceReference)
    {
        BootstrapPropertiesService bootstrapPropertiesService = (BootstrapPropertiesService) bundleContext.getService(serviceReference);
        registryBootstrapService.register(bootstrapPropertiesService);
    }

    @Override
    protected void doUnregisterService(ServiceReference serviceReference)
    {
        BootstrapPropertiesService bootstrapPropertiesService = (BootstrapPropertiesService) bundleContext.getService(serviceReference);
        registryBootstrapService.unregister(bootstrapPropertiesService);
        bundleContext.ungetService(serviceReference);
    }

    public static RegistryBootstrapServiceWrapper createServiceWrapper(RegistryBootstrapService registryBootstrapService, BundleContext bundleContext)
    {
        RegistryBootstrapServiceWrapper bootstrapServiceWrapper = new RegistryBootstrapServiceWrapper(bundleContext, registryBootstrapService);
        try
        {
            String filter = "(objectclass=" + BootstrapPropertiesService.class.getName() + ")";
            bundleContext.addServiceListener(bootstrapServiceWrapper, filter);

            Collection<ServiceReference<BootstrapPropertiesService>> serviceReferences = bundleContext.getServiceReferences(BootstrapPropertiesService.class, null);

            for (ServiceReference<BootstrapPropertiesService> serviceReference : serviceReferences)
            {
                bootstrapServiceWrapper.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, serviceReference));
            }
        }
        catch (InvalidSyntaxException e)
        {
            throw new IllegalStateException(e);
        }

        return bootstrapServiceWrapper;
    }
}
