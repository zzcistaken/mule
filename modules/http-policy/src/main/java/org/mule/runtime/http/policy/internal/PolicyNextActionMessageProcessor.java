/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.http.policy.internal;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.message.InternalMessage;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.exception.MessagingException;
import org.mule.runtime.core.execution.CreateResponseParametersFunction;
import org.mule.runtime.dsl.api.component.ComponentIdentifier;

public class PolicyNextActionMessageProcessor implements Processor
{
    private Processor next;
    private CreateResponseParametersFunction se;
    private CreateResponseParametersFunction fe;
    private ComponentIdentifier targetComponent;

    public void setTargetComponent(ComponentIdentifier targetComponent)
    {
        this.targetComponent = targetComponent;
    }

    @Override
    public Event process(Event event) throws MuleException
    {
        try {
            return next.process(event);
            //Event process = next.process(event);
            //Optional<PolicyOperationParametersTransformer> policyOperationParametersTransformer = policyManager.lookupOperationParametersTransformer(targetComponent);
            //if (policyOperationParametersTransformer.isPresent()) {
            //    return Event.builder(event).message((InternalMessage) policyOperationParametersTransformer.get().fromParametersToMessage(se.createResponseParameters(process))).build();
            //} else {
            //    return Event.builder(event).message(process.getMessage()).build();
            //}
        }
        catch (MessagingException e) {
            return Event.builder(event).message((InternalMessage) fe.createResponseParameters(e.getEvent())).build();
        }
    }

    public void setNext(Processor next, CreateResponseParametersFunction se, CreateResponseParametersFunction fe)
    {
        this.next = next;
        this.se = se;
        this.fe = fe;
    }
}
