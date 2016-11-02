/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.http.policy.internal;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.policy.Policy;
import org.mule.runtime.core.policy.OperationPolicyInstance;
import org.mule.runtime.dsl.api.component.ComponentIdentifier;

import java.util.function.Function;

public class HttpProxyPolicy implements Policy
{

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
    public boolean appliesToSource(ComponentIdentifier sourceIdentifier)
    {
        return sourceIdentifier.toString().equals("http:listener");
    }

    @Override
    public boolean appliesToOperation(ComponentIdentifier operationIdentifier)
    {
        return operationIdentifier.toString().equals("http:request");
    }

    @Override
    public OperationPolicyInstance createSourcePolicyInstance(ComponentIdentifier operationIdentifier)
    {
        return new OperationPolicyInstance()
        {

            @Override
            public Event processSource(Event sourceMessage, Function<Event, Event> next) throws MuleException
            {
                source.replaceNext(next);
                return source.process(sourceMessage);
            }

            @Override
            public Event processOperation(Event eventBeforeOperation, Function<Event, Event> next) throws MuleException
            {
                request.replaceNext(next);
                return request.process(eventBeforeOperation);
            }

            @Override
            public Policy getOperationPolicy()
            {
                return HttpProxyPolicy.this;
            }
        };
    }
}
