/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.deployment.model.api.domain;

import org.mule.runtime.deployment.model.api.artifact.ArtifactFactory;
import org.mule.runtime.deployment.model.api.factory.ArtifactFactoryProvider;

/**
 * Factory for {@link Domain} artifact creation
 */
public interface DomainFactory extends ArtifactFactory<Domain> {

  /**
   * Uses an {@link ArtifactFactoryProvider} to determine the implementation of {@link DomainFactory} to use.
   * 
   * @return the {@link DomainFactory} available in the current context.
   */
  static DomainFactory discover() {
    return ArtifactFactoryProvider.getDefaultProvider().getDomainFactory();
  }
}
