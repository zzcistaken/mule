/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.http.policy.internal;

import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.execution.NextOperation;
import org.mule.runtime.dsl.api.component.ComponentIdentifier;

import java.util.function.Consumer;

public class HttpRequest extends AbstractPolicyChain {

  @Override
  ComponentIdentifier getTargetComponentIdentifier() {
    return new ComponentIdentifier.Builder().withName("request").withNamespace("httpn").build();
  }

  @Override
  public NextOperation createNextOperation(String id, Consumer<Event> eventStackConsumer, NextOperation next)
  {
    NextOperation nextOperation = super.createNextOperation(id, eventStackConsumer, next);
    return (event -> {
      return nextOperation.execute(event);
    });
  }
}
