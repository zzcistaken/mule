/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.exception;

import static java.util.Collections.singletonList;
import static reactor.core.publisher.Mono.from;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.processor.Processor;

import org.reactivestreams.Publisher;

/**
 * Factory object for {@link ErrorHandler}.
 *
 * @since 4.0
 */
public class ErrorHandlerFactory {

  public ErrorHandler createDefault(MuleContext muleContext) {
    ErrorHandler errorHandler = new ErrorHandler();
    errorHandler.setMuleContext(muleContext);
    OnErrorPropagateHandler onErrorPropagateHandler = new OnErrorPropagateHandler();
    onErrorPropagateHandler.setMessageProcessors(singletonList(new PayloadNullifyingProcessor()));
    errorHandler.setExceptionListeners(singletonList(onErrorPropagateHandler));
    return errorHandler;
  }

  private static class PayloadNullifyingProcessor implements Processor {

    @Override
    public Event process(Event event) throws MuleException {
      return Event.builder(event).message(Message.builder(event.getMessage()).payload(null).build()).build();
    }

    @Override
    public Publisher<Event> apply(Publisher<Event> publisher) {
      return from(publisher).map(
                                 event -> Event.builder(event).message(Message.builder(event.getMessage()).payload(null).build())
                                     .build());
    }

  }
}
