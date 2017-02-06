/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mule.runtime.core.api.rx.Exceptions.UNEXPECTED_EXCEPTION_PREDICATE;
import static org.mule.runtime.core.api.scheduler.SchedulerConfig.config;
import static org.mule.runtime.core.context.notification.AsyncMessageNotification.PROCESS_ASYNC_COMPLETE;
import static org.mule.runtime.core.context.notification.AsyncMessageNotification.PROCESS_ASYNC_SCHEDULED;
import static org.mule.runtime.core.processor.strategy.ProcessingStrategyUtils.FAIL_IF_TX_ACTIVE_EVENT_CONSUMER;
import static org.slf4j.LoggerFactory.getLogger;
import static reactor.core.publisher.Flux.from;
import static reactor.core.publisher.Flux.just;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.scheduler.Schedulers.fromExecutorService;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.construct.Pipeline;
import org.mule.runtime.core.api.exception.MessagingExceptionHandler;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategyFactory;
import org.mule.runtime.core.api.rx.Exceptions.EventDroppedException;
import org.mule.runtime.core.context.notification.AsyncMessageNotification;
import org.mule.runtime.core.exception.MessagingException;
import org.mule.runtime.core.session.DefaultMuleSession;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.resource.spi.work.WorkManager;

import org.slf4j.Logger;

/**
 * This factory's strategy uses a {@link WorkManager} to schedule the processing of the pipeline of message processors in a single
 * worker thread.
 */
@Deprecated
public class LegacyAsynchronousProcessingStrategyFactory implements ProcessingStrategyFactory {

  private static final Logger LOGGER = getLogger(LegacyAsynchronousProcessingStrategyFactory.class);

  public static final String SYNCHRONOUS_EVENT_ERROR_MESSAGE = "Unable to process a transactional flow asynchronously";

  @Override
  public ProcessingStrategy create(MuleContext muleContext, String schedulersNamePrefix) {
    return new LegacyAsynchronousProcessingStrategy(() -> muleContext.getSchedulerService()
        .ioScheduler(config().withName(schedulersNamePrefix)),
                                                    scheduler -> scheduler
                                                        .stop(muleContext.getConfiguration().getShutdownTimeout(), MILLISECONDS),
                                                    FAIL_IF_TX_ACTIVE_EVENT_CONSUMER,
                                                    muleContext);
  }

  @Deprecated
  static class LegacyAsynchronousProcessingStrategy extends AbstractLegacyProcessingStrategy
      implements Startable, Stoppable {

    private Consumer<Scheduler> schedulerStopper;
    private MuleContext muleContext;
    private Supplier<Scheduler> schedulerSupplier;
    private Scheduler scheduler;
    private Consumer<Event> eventConsumer;

    public LegacyAsynchronousProcessingStrategy(Supplier<Scheduler> schedulerSupplier, Consumer<Scheduler> schedulerStopper,
                                                Consumer<Event> eventConsumer, MuleContext muleContext) {
      this.schedulerSupplier = schedulerSupplier;
      this.schedulerStopper = schedulerStopper;
      this.eventConsumer = eventConsumer;
      this.muleContext = muleContext;
    }

    @Override
    public Function<ReactiveProcessor, ReactiveProcessor> onPipeline(Pipeline flowConstruct,
                                                                     MessagingExceptionHandler messagingExceptionHandler) {

      // Conserve existing 3.x async processing strategy behaviuor:
      // i) The request event is echoed rather than the the result of async processing returned
      // ii) Any exceptions that occur due to async processing are not propagated upwards
      return processor -> publisher -> from(publisher)
          .doOnNext(eventConsumer)
          .doOnNext(fireAsyncScheduledNotification(flowConstruct))
          .doOnNext(request -> just(request)
              .map(event -> Event.builder(event).session(new DefaultMuleSession(event.getSession())).build())
              .publishOn(fromExecutorService(scheduler))
              .transform(processor)
              .doOnNext(event -> fireAsyncCompleteNotification(event, flowConstruct, null))
              .doOnError(MessagingException.class, e -> fireAsyncCompleteNotification(e.getEvent(), flowConstruct, e))
              .onErrorResumeWith(MessagingException.class, messagingExceptionHandler)
              .onErrorResumeWith(EventDroppedException.class, mde -> empty())
              .doOnError(UNEXPECTED_EXCEPTION_PREDICATE, exception -> LOGGER.error("Unhandled exception in async processing.",
                                                                                   exception))
              .subscribe());
    }

    @Override
    public void start() throws MuleException {
      this.scheduler = schedulerSupplier.get();
    }

    @Override
    public void stop() throws MuleException {
      schedulerStopper.accept(scheduler);
    }

    protected Consumer<Event> fireAsyncScheduledNotification(FlowConstruct flowConstruct) {
      return event -> muleContext.getNotificationManager()
          .fireNotification(new AsyncMessageNotification(flowConstruct, event, null, PROCESS_ASYNC_SCHEDULED));
    }

    protected void fireAsyncCompleteNotification(Event event, FlowConstruct flowConstruct, MessagingException exception) {
      muleContext.getNotificationManager()
          .fireNotification(new AsyncMessageNotification(flowConstruct, event, null, PROCESS_ASYNC_COMPLETE, exception));
    }

  }

}
