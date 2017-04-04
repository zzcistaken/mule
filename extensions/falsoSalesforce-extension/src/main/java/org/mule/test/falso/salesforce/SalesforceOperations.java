/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.falso.salesforce;

import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.runtime.streaming.PagingProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SalesforceOperations {

  public PagingProvider<FalsaSalesforceConnection, FalsoClient> queryClients(Integer startingId,
                                                                             @Connection FalsaSalesforceConnection c) {
    return new PagingProvider<FalsaSalesforceConnection, FalsoClient>() {

      private boolean hit = false;

      @Override
      public List<FalsoClient> getPage(FalsaSalesforceConnection connection) {
        if (!hit) {
          hit = true;
          return connection.getClientsSince(startingId);
        }

        return Collections.emptyList();
      }

      @Override
      public Optional<Integer> getTotalResults(FalsaSalesforceConnection connection) {
        return Optional.empty();
      }

      @Override
      public void close() throws IOException {

      }
    };
  }
}
