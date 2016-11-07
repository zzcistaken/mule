/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.http.policy.internal;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.message.InternalMessage;
import org.mule.runtime.core.api.policy.PolicyOperationParametersTransformer;
import org.mule.runtime.core.execution.FlowExecutionFunction;
import org.mule.runtime.core.execution.CreateResponseParametersFunction;
import org.mule.runtime.core.policy.Policy;
import org.mule.runtime.core.policy.OperationPolicyInstance;
import org.mule.runtime.core.policy.PolicyManager;
import org.mule.runtime.dsl.api.component.ComponentIdentifier;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;

public class HttpProxyPolicy implements Policy
{

    @Inject
    private PolicyManager policyManager;
    private HttpRequest request;
    private HttpSource source;

    public HttpRequest getRequest()
    {
        return request;
    }

    public void setRequest(HttpRequest request)
    {
        this.request = request;
    }

    public HttpSource getSource()
    {
        return source;
    }

    public void setSource(HttpSource source)
    {
        this.source = source;
    }

    @Override
    public OperationPolicyInstance createOperationPolicyInstance(String id, ComponentIdentifier sourceIdentifie )
    {
        return new OperationPolicyInstance()
        {

            @Override
            public Event process(Event eventBeforeOperation, Function<Event, Event> next) throws MuleException
            {
                request.replaceNext(next);
                return request.process(eventBeforeOperation);
            }

            @Override
            public Event process(Event event, FlowExecutionFunction flowExecution, CreateResponseParametersFunction successExecutionCreateResponseParametersFunction, CreateResponseParametersFunction failedExecutionCreateResponseParametersFunction)
            {
                return null;
            }
        };
    }

    @Override
    public OperationPolicyInstance createSourcePolicyInstance(String id, final ComponentIdentifier sourceIdentifier)
    {
        return new OperationPolicyInstance()
        {

            @Override
            public Event process(Event eventBeforeOperation, Function<Event, Event> next) throws MuleException
            {
                request.replaceNext(next);
                return request.process(eventBeforeOperation);
            }

            @Override
            public Event process(Event event, FlowExecutionFunction flowExecution, CreateResponseParametersFunction successExecutionCreateResponseParametersFunction, CreateResponseParametersFunction failedExecutionCreateResponseParametersFunction)
            {
                FlowExecutionFunction modifiedFlowExecutionFunction = (processEvent) -> {
                    Event process = flowExecution.execute(event);
                    Optional<PolicyOperationParametersTransformer> policyOperationParametersTransformer = policyManager.lookupOperationParametersTransformer(sourceIdentifier);
                    if (policyOperationParametersTransformer.isPresent()) {
                        Map<String, Object> responseParameters = successExecutionCreateResponseParametersFunction.createResponseParameters(process);
                        return Event.builder(event).message((InternalMessage) policyOperationParametersTransformer.get().fromParametersToMessage(responseParameters)).build();
                    } else {
                        return Event.builder(event).message(process.getMessage()).build();
                    }
                };
                source.replaceNext(event, modifiedFlowExecutionFunction, successExecutionCreateResponseParametersFunction, failedExecutionCreateResponseParametersFunction);
                try
                {
                    return source.process(event);
                }
                catch (MuleException e)
                {
                    throw new MuleRuntimeException(e);
                }
            }
        };
    }
}
