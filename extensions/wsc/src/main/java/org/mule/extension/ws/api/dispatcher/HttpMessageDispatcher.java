/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.ws.api.dispatcher;

import static org.mule.extension.http.api.HttpHeaders.Names.CONTENT_TYPE;
import static org.mule.runtime.extension.api.client.DefaultOperationParameters.builder;
import static org.mule.service.http.api.HttpConstants.Method.POST;
import static org.mule.service.http.api.HttpConstants.Protocols.HTTP;
import org.mule.extension.http.api.HttpAttributes;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.runtime.ConfigurationProvider;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.service.http.api.domain.ParameterMap;
import org.mule.services.soap.api.client.DispatcherResponse;

import java.io.InputStream;
import java.util.Map;

public class HttpMessageDispatcher extends AbstractWscMessageDispatcher {

  private static final String http = HTTP.name();

  @Override
  protected void validate(ConfigurationProvider configurationProvider) {
    if (!configurationProvider.getExtensionModel().getName().equals(http)) {
      throw new IllegalArgumentException("is not an 'http'");
    }

    if (!configurationProvider.getConfigurationModel().getName().equals("request-config")) {
      throw new IllegalArgumentException("is not an 'request-config' config");
    }
  }

  @Override
  protected DispatcherResponse doDispatch(String address, InputStream message, Map<String, String> properties)
      throws MuleException {
    ParameterMap parameters = new ParameterMap();
    parameters.putAll(properties);

    // todo client gets stucked here
    Result<?, HttpAttributes> result = client.execute(http, "request", builder()
        .configName(configName)
        .addParameter("method", POST.name())
        .addParameter("url", address)
        .addParameter("body", message)
        .addParameter("headers", parameters)
        .build());

    return new DispatcherResponse(result.getAttributes().get().getHeaders().get(CONTENT_TYPE), (InputStream) result.getOutput());
  }
}
