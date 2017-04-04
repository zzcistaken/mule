/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.falso.salesforce;

import java.util.LinkedList;
import java.util.List;

public class FalsaSalesforceConnection {

  private LinkedList<FalsoClient> clients = new LinkedList<>();
  private int index = -1;

  public List<FalsoClient> getClientsSince(Integer startIndex) {
    if (startIndex == null || startIndex < 1) {
      startIndex = 100;
    }

    for (int i = 0; i < startIndex; i++) {
      FalsoClient client = new FalsoClient();
      client.setId(++index);
      client.setName("Pepita la pistolera");
      client.setAddress("Av. Siempre Viva 742, Springfield");

      clients.add(client);
    }

    return clients.subList(0, startIndex);
  }
}
