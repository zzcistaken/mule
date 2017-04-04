/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.falso.salesforce;

import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;

public class FalsoSalesforceConnectionProvider implements CachedConnectionProvider<FalsaSalesforceConnection> {

  @Override
  public FalsaSalesforceConnection connect() throws ConnectionException {
    return new FalsaSalesforceConnection();
  }

  @Override
  public void disconnect(FalsaSalesforceConnection connection) {

  }

  @Override
  public ConnectionValidationResult validate(FalsaSalesforceConnection connection) {
    return ConnectionValidationResult.success();
  }
}
