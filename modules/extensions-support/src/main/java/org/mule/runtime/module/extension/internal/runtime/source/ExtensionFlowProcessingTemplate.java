/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.runtime.source;

import org.mule.runtime.core.execution.CompletionHandler;
import org.mule.runtime.api.message.MuleEvent;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.exception.MessagingException;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.execution.AsyncResponseFlowProcessingPhaseTemplate;
import org.mule.runtime.core.execution.CreateResponseParametersFunction;
import org.mule.runtime.core.execution.ResponseCompletionCallback;

import java.util.Map;
import java.util.Optional;

final class ExtensionFlowProcessingTemplate implements AsyncResponseFlowProcessingPhaseTemplate {

  private final Event event;
  private final Processor messageProcessor;
  private final CompletionHandler<Event, MessagingException> completionHandler;
  private final Optional<Object> messagePolicyDescriptor;

  ExtensionFlowProcessingTemplate(Event event,
                                  Processor messageProcessor,
                                  CompletionHandler<Event, MessagingException> completionHandler, Optional<Object> messagePolicyDescriptor) {
    this.event = event;
    this.messageProcessor = messageProcessor;
    this.completionHandler = completionHandler;
    this.messagePolicyDescriptor = messagePolicyDescriptor;
  }

  public Optional<Object> getMessagePolicyDescriptor()
  {
    return messagePolicyDescriptor;
  }

  @Override
  public CreateResponseParametersFunction getSuccessfulExecutionMessageCreationFunction()
  {
    if (completionHandler instanceof SourceAdapter.SourceCompletionHandler) {
      return (event -> ((SourceAdapter.SourceCompletionHandler) completionHandler).createResponseParameters(event));
    }
    return null;
  }

  @Override
  public CreateResponseParametersFunction getFailedExecutionMessageCreationFunction()
  {
    return null;
  }

  @Override
  public Event getEvent() throws MuleException {
    return event;
  }

  @Override
  public Event routeEvent(Event muleEvent) throws MuleException {
    return messageProcessor.process(muleEvent);
  }

  @Override
  public void sendResponseToClient(Event event, ResponseCompletionCallback responseCompletionCallback)
      throws MuleException {
    //for (OperationPolicyInstance policyInstance : muleEvent.getPolicyInstances())
    //{
    //  ComponentIdentifier componentIdentifier = new ComponentIdentifier.Builder().withNamespace("httpn").withNamespace(sourceModel.getName()).build();
    //  if (policyInstance.getOperationPolicy().appliesToSource(componentIdentifier))
    //  {
    //    //TODO fix.
    //    muleEvent = policyInstance.processSource(muleEvent, null);
    //  }
    //}
    //final org.mule.runtime.core.api.Event resultEvent = muleEvent;
    ExtensionSourceExceptionCallback exceptionCallback =
        new ExtensionSourceExceptionCallback(responseCompletionCallback, event, completionHandler::onFailure);
    runAndNotify(() -> completionHandler.onCompletion(event, null, exceptionCallback), this.event, responseCompletionCallback);
  }

  @Override
  public void sendResponseToClient(Event event, Map<String, Object> parameters, ResponseCompletionCallback responseCompletionCallback)
          throws MuleException {
    ExtensionSourceExceptionCallback exceptionCallback =
            new ExtensionSourceExceptionCallback(responseCompletionCallback, event, completionHandler::onFailure);
    runAndNotify(() -> completionHandler.onCompletion(event, parameters, exceptionCallback), this.event, responseCompletionCallback);
  }

  @Override
  public void sendFailureResponseToClient(MessagingException messagingException,
                                          ResponseCompletionCallback responseCompletionCallback)
      throws MuleException {
    runAndNotify(() -> completionHandler.onFailure(messagingException), event, responseCompletionCallback);
  }

  private void runAndNotify(Runnable runnable, MuleEvent event, ResponseCompletionCallback responseCompletionCallback) {
    try {
      runnable.run();
      responseCompletionCallback.responseSentSuccessfully();
    } catch (Exception e) {
      responseCompletionCallback.responseSentWithFailure(new MessagingException((Event) event, e),
                                                         (Event) event);
    }
  }
}
