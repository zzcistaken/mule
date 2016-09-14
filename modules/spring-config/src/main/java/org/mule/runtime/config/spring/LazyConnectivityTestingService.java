/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring;

import static org.mule.runtime.api.connection.ConnectionExceptionCode.UNKNOWN;
import static org.mule.runtime.api.connection.ConnectionValidationResult.failure;
import static org.mule.runtime.core.config.i18n.I18nMessageFactory.createStaticMessage;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.config.spring.dsl.model.NoSuchComponentModelException;
import org.mule.runtime.core.api.connectivity.ConnectivityTestingObjectNotFoundException;
import org.mule.runtime.core.api.connectivity.ConnectivityTestingService;

public class LazyConnectivityTestingService implements ConnectivityTestingService {

  private final LazyComponentResolver lazyComponentResolver;
  private final ConnectivityTestingService connectivityTestingService;

  public LazyConnectivityTestingService(LazyComponentResolver lazyComponentResolver,
                                        ConnectivityTestingService connectivityTestingService) {
    this.lazyComponentResolver = lazyComponentResolver;
    this.connectivityTestingService = connectivityTestingService;
  }

  @Override
  public ConnectionValidationResult testConnection(String identifier) {
    try {
      lazyComponentResolver.initializeComponent(identifier);
    } catch (NoSuchComponentModelException e) {
      throw new ConnectivityTestingObjectNotFoundException(identifier);
    } catch (Exception e) {
      return failure(e.getMessage(), UNKNOWN, e);
    }
    return connectivityTestingService.testConnection(identifier);
  }
}
