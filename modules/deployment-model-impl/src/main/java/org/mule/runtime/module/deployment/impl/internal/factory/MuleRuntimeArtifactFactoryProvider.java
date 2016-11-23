/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.deployment.impl.internal.factory;

import org.mule.runtime.deployment.model.api.application.ApplicationFactory;
import org.mule.runtime.deployment.model.api.artifact.ArtifactFactory;
import org.mule.runtime.deployment.model.api.domain.DomainFactory;
import org.mule.runtime.deployment.model.api.factory.ArtifactFactoryProvider;
import org.mule.runtime.module.deployment.impl.internal.MuleArtifactResourcesRegistry;

/**
 * Provides the {@link ArtifactFactory}s for the Mule Runtime.
 *
 * @since 4.0
 */
public class MuleRuntimeArtifactFactoryProvider extends ArtifactFactoryProvider {

  @Override
  public ApplicationFactory getApplicationFactory() {
    return getRegistry().getApplicationFactory();
  }

  @Override
  public DomainFactory getDomainFactory() {
    return getRegistry().getDomainFactory();
  }

  /**
   * @return a fresh registry with the default artifact factories for the Mule Runtime.
   */
  public MuleArtifactResourcesRegistry getRegistry() {
    return new MuleArtifactResourcesRegistry();
  }
}
