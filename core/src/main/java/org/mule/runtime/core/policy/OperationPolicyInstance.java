/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.policy;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.execution.FlowExecutionFunction;
import org.mule.runtime.core.execution.CreateResponseParametersFunction;

import java.io.Serializable;
import java.util.function.Function;

public interface OperationPolicyInstance extends Serializable {

  Event process(Event eventBeforeOperation, Function<Event, Event> next) throws MuleException;

  Event process(Event event, FlowExecutionFunction flowExecution, CreateResponseParametersFunction successExecutionCreateResponseParametersFunction, CreateResponseParametersFunction failedExecutionCreateResponseParametersFunction);
}
