/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.deployment.model.api.factory;

import static java.lang.String.format;
import static java.util.ServiceLoader.load;

import org.mule.runtime.deployment.model.api.application.ApplicationFactory;
import org.mule.runtime.deployment.model.api.domain.DomainFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for {@link ApplicationFactory} implementations.
 *
 * @since 4.0
 */
public abstract class ArtifactFactoryProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactFactoryProvider.class);

  static {
    try {
      final ArtifactFactoryProvider provider = load(ArtifactFactoryProvider.class).iterator().next();
      LOGGER.info(format("Loaded ApplicationFactoryProvider impementation '%s' form classloader '%s'",
                         provider.getClass().getName(), provider.getClass().getClassLoader().toString()));

      DEFAULT_PROVIDER = provider;
    } catch (Exception e) {
      LOGGER.error("Error loading ApplicationFactoryProvider implementation.", e);
      throw e;
    }
  }

  private static final ArtifactFactoryProvider DEFAULT_PROVIDER;

  /**
   * The implementation of this abstract class is provided by the module containing such implementation, and loaded during this
   * class initialization.
   * <p>
   * If more than one implementation is found, the classLoading order of those implementations will determine which one is used.
   * Information about this will be logged to aid in the troubleshooting of those cases.
   *
   * @return the implementation of this builder factory provided by the Mule Runtime.
   */
  public static final ArtifactFactoryProvider getDefaultProvider() {
    return DEFAULT_PROVIDER;
  }

  /**
   * @return a fresh {@link ApplicationFactory} object.
   */
  public abstract ApplicationFactory getApplicationFactory();

  /**
   * @return a fresh {@link DomainFactory} object.
   */
  public abstract DomainFactory getDomainFactory();

}
