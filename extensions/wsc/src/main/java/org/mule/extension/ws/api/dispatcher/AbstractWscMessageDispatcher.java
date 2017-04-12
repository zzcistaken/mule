/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.ws.api.dispatcher;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.core.api.extension.ExtensionManager;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.client.ExtensionsClient;
import org.mule.runtime.extension.api.runtime.ConfigurationProvider;
import org.mule.services.soap.api.client.DispatcherResponse;
import org.mule.services.soap.api.client.MessageDispatcher;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractWscMessageDispatcher implements MessageDispatcher {

  protected ExtensionManager extensionManager;
  protected ExtensionsClient client;

  @Parameter
  String configName;

  public void configure(ExtensionManager manager, ExtensionsClient extensionsClient) {
    this.extensionManager = manager;
    this.client = extensionsClient;
  }

  protected abstract void validate(ConfigurationProvider configurationProvider);

  protected abstract DispatcherResponse doDispatch(String address, InputStream message, Map<String, String> properties) throws MuleException;

  @Override
  public void dispose() {

  }

  @Override
  public void initialise() throws InitialisationException {}

  @Override
  public DispatcherResponse dispatch(String address, InputStream message, Map<String, String> properties) {
    Optional<ConfigurationProvider> configurationProvider = extensionManager.getConfigurationProvider(configName);

    if (!configurationProvider.isPresent()) {
      throw new IllegalArgumentException("no config");
    }

    validate(configurationProvider.get());

    try {
      return doDispatch(address, message, properties);
    } catch (MuleException e) {
      throw new RuntimeException("");
    }
  }
}
