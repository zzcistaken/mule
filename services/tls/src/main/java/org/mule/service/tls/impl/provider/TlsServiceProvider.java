/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.service.tls.impl.provider;

import static java.util.Collections.singletonList;
import org.mule.runtime.api.service.ServiceDefinition;
import org.mule.runtime.api.service.ServiceProvider;
import org.mule.service.tls.impl.TlsServiceImplementation;
import org.mule.service.tls.api.TlsService;

import java.util.List;

/**
 * Created by anafelisatti on 3/24/17.
 */
public class TlsServiceProvider implements ServiceProvider {

  @Override
  public List<ServiceDefinition> providedServices() {
    TlsService tlsService = new TlsServiceImplementation();
    ServiceDefinition serviceDefinition = new ServiceDefinition(TlsService.class, tlsService);
    return singletonList(serviceDefinition);
  }
}
