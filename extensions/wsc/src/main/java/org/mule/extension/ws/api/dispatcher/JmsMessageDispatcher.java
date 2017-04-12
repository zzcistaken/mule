/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.ws.api.dispatcher;

import static org.mule.runtime.extension.api.client.DefaultOperationParameters.builder;
import org.mule.extensions.jms.api.message.JmsxProperties;
import org.mule.extensions.jms.api.message.MessageBuilder;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Attributes;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.ConfigurationProvider;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.services.soap.api.client.DispatcherResponse;

import java.io.InputStream;
import java.util.Map;

public class JmsMessageDispatcher extends AbstractWscMessageDispatcher {

  @Override
  protected void validate(ConfigurationProvider configurationProvider) {
    //todo check config
  }

  @Override
  protected DispatcherResponse doDispatch(String address, InputStream message, Map<String, String> properties)
      throws MuleException {
    Result<?, Attributes> result = client.execute("JMS", "publishConsume", builder()
        .configName(configName)
        .addParameter("messageBuilder", MessageBuilder.class, builder()
            .addParameter("body", new TypedValue(message, DataType.BYTE_ARRAY))
            .addParameter("jmsxProperties", JmsxProperties.class, builder())  // TODO fix @NullSafe
            .addParameter("properties", properties))
        .addParameter("destination", address)
        .build());

    //todo content type?
    return new DispatcherResponse("", (InputStream) result.getOutput());
  }
}
