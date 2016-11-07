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
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.lifecycle.Initialisable;
import org.mule.runtime.core.api.lifecycle.InitialisationException;
import org.mule.runtime.core.api.lifecycle.LifecycleUtils;
import org.mule.runtime.core.api.policy.PolicyOperationParametersTransformer;
import org.mule.runtime.core.api.processor.MessageProcessorChain;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.execution.FlowExecutionFunction;
import org.mule.runtime.core.execution.CreateResponseParametersFunction;
import org.mule.runtime.core.policy.PolicyManager;
import org.mule.runtime.core.processor.chain.DefaultMessageProcessorChainBuilder;
import org.mule.runtime.dsl.api.component.ComponentIdentifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;

public abstract class AbstractPolicyChain implements Processor, Initialisable
{

    @Inject
    private MuleContext muleContext;
    @Inject
    private PolicyManager policyManager;

    private List<Processor> processors;
    private MessageProcessorChain processorChain;

    public void setProcessors(List<Processor> processors)
    {
        this.processors = processors;
    }

    @Override
    public Event process(Event event) throws MuleException
    {
        initialise();
        Event result = processorChain.process(event);
        return result;
    }

    public void replaceNext(Function<Event, Event> next) {
        processors.stream()
                .forEach(processor -> {
                    if (processor instanceof PolicyNextActionMessageProcessor) {
                        ((PolicyNextActionMessageProcessor) processor).setNext(event -> next.apply(event), null, null);
                    }
                });
    }

    @Override
    public final void initialise() throws InitialisationException
    {
        LifecycleUtils.initialiseIfNeeded(processors, muleContext);
        processorChain = new DefaultMessageProcessorChainBuilder().chain(processors).build();
        processorChain.setMuleContext(muleContext);
        processorChain.initialise();
    }

    public void replaceNext(final Event event, FlowExecutionFunction flowExecution, CreateResponseParametersFunction successExecutionCreateResponseParametersFunction, CreateResponseParametersFunction failedExecutionCreateResponseParametersFunction)
    {
        processors.stream()
                .forEach(processor -> {
                    if (processor instanceof PolicyNextActionMessageProcessor) {
                        //TODO make this only in the first intialization
                        ((PolicyNextActionMessageProcessor) processor).setTargetComponent(getTargetComponentIdentifier());
                        ((PolicyNextActionMessageProcessor) processor).setNext(muleEvent -> {
                            try
                            {
                                return flowExecution.execute(event);
                            }
                            catch (Exception e)
                            {
                                throw new MuleRuntimeException(e);
                            }
                        }, successExecutionCreateResponseParametersFunction, failedExecutionCreateResponseParametersFunction);
                    }
                });
    }

    abstract ComponentIdentifier getTargetComponentIdentifier();

}
