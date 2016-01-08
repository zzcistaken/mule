/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.deployer.extension;

import org.mule.api.lifecycle.Lifecycle;
import org.mule.deployer.api.DeploymentServiceAware;

/**
 * Manages lifecycle and dependency injection for {@link org.mule.MuleCoreExtension}
 */
//TODO(pablo.kraan): OSGi - is still needed to implement PluginClassLoaderManagerAware?
public interface MuleCoreExtensionManager extends Lifecycle, DeploymentServiceAware
{

}
