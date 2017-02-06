/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static java.lang.Math.min;
import static java.lang.Runtime.getRuntime;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;
import static org.mule.runtime.core.transaction.TransactionCoordination.isTransactionActive;
import static reactor.core.Exceptions.propagate;
import static reactor.core.publisher.Flux.from;
import static reactor.core.publisher.Flux.just;
import static reactor.core.scheduler.Schedulers.fromExecutorService;

import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.core.api.DefaultMuleException;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.api.processor.ReactiveProcessor.ProcessingType;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;
import org.mule.runtime.core.exception.MessagingException;
import org.mule.runtime.core.internal.util.rx.ConditionalExecutorServiceDecorator;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Utility method to be used by {@link ProcessingStrategy} implementations given simple inheritance doesn't give the required
 * flexibility.
 */
final class ProcessingStrategyUtils {

  public static final String TRANSACTIONAL_ERROR_MESSAGE = "Unable to process a transactional flow asynchronously";

  static final Consumer<Event> NOP_EVENT_CONSUMER = event -> {
  };

  static final Consumer<Event> FAIL_IF_TX_ACTIVE_EVENT_CONSUMER = event -> {
    if (isTransactionActive()) {
      throw propagate(new MessagingException(event,
                                             new DefaultMuleException(createStaticMessage(TRANSACTIONAL_ERROR_MESSAGE))));
    }
  };

  /**
   * Creates a {@link ReactiveProcessor} function that decorates a {@link ProcessingType#BLOCKING} processor by distributing tasks
   * on a dedicated thread pool for blocking processors.
   *
   * @param blockingScheduler the {@link ProcessingType#BLOCKING} executor.
   * @param maxConcurrency maximum number of concurrent of {@link ProcessingType#BLOCKING} processors that can be processed
   *        currently on the IO thread pool. This should be less than the number of threads availble in the thread pool.
   * @return the {@link ReactiveProcessor} decorating function.
   */
  static Function<ReactiveProcessor, ReactiveProcessor> scheduleBlocking(Scheduler blockingScheduler,
                                                                         int maxConcurrency) {
    return scheduleBlockingProactor(blockingScheduler, null, maxConcurrency);
  }

  /**
   * Creates a {@link ReactiveProcessor} function that decorates a {@link ProcessingType#BLOCKING} processor by distributing tasks
   * on a dedicated thread pool for blocking processors and then continuing execution on another scheduler. Once processing of the
   * given task is complete, processing continues on the same thread.
   *
   * @param blockingScheduler the executor used to schedule processors.
   * @param publishOnScheduler the executor to publish on once blocking or cpu intensive processor is complete.
   * @param maxConcurrency maximum number of concurrent of {@link ProcessingType#BLOCKING} processors that can be processed
   *        currently on the IO thread pool. This should be less than the number of threads availble in the thread pool.
   * @return the {@link ReactiveProcessor} decorating function.
   */
  static Function<ReactiveProcessor, ReactiveProcessor> scheduleBlockingProactor(Scheduler blockingScheduler,
                                                                                 Scheduler publishOnScheduler,
                                                                                 int maxConcurrency) {
    reactor.core.scheduler.Scheduler reactorBlockingScheduler = createReactorScheduler(blockingScheduler);
    reactor.core.scheduler.Scheduler reactorPublishOnScheduler = createReactorScheduler(publishOnScheduler);
    return processor -> publisher -> from(publisher)
        .flatMap(event -> {
          // Only schedule if no transaction is active.
          if (!isTransactionActive()) {
            Mono<Event> mono = Mono.just(event)
                .transform(processor)
                .subscribeOn(reactorBlockingScheduler);
            if (publishOnScheduler != null) {
              return mono.publishOn(reactorPublishOnScheduler);
            } else {
              return mono;
            }
          } else {
            return just(event);
          }
        }, maxConcurrency);
  }

  /**
   * Creates a {@link ReactiveProcessor} function that decorates a {@link ProcessingType#CPU_INTENSIVE} or
   * {@link ProcessingType#CPU_LITE} processor by distributing tasks on a dedicated thread pool. This may be used to distribute
   * non-blocking processor on a shared CPU_LITE pool from a single source or event-loop thread, or for scheduling CPU intensive
   * tasks. Once processing of the given task is complete, processing continues on the same thread.
   *
   * @param parallelScheduler the {@link ProcessingType#CPU_INTENSIVE} scheduler.
   * @param maxConcurrency maximum number of concurrent of {@link ProcessingType#BLOCKING} processors that can be processed
   *        currently on the IO thread pool. This should be less than the number of threads available in the thread pool.
   * @return the {@link ReactiveProcessor} decorating function.
   */
  static Function<ReactiveProcessor, ReactiveProcessor> scheduleParallel(Scheduler parallelScheduler,
                                                                         int maxConcurrency) {
    return scheduleParallelProactor(parallelScheduler, null, maxConcurrency);
  }

  /**
   * Creates a {@link ReactiveProcessor} function that decorates a {@link ProcessingType#CPU_INTENSIVE} processor to distribute
   * tasks on a dedicated thread pool for this type of task and continuing execution using another scheduler once work is
   * complete.
   *
   * @param parallelScheduler the executor used to schedule processors.
   * @param publishOnScheduler the executor to publish on once blocking or cpu intensive processor is complete.
   * @param maxConcurrency maximum number of concurrent of {@link ProcessingType#CPU_INTENSIVE} processors that can be processed
   *        currently on the IO thread pool. This should be less than the number of threads availble in the thread pool.
   * @return the {@link ReactiveProcessor} decorating function.
   */
  static Function<ReactiveProcessor, ReactiveProcessor> scheduleParallelProactor(Scheduler parallelScheduler,
                                                                                 Scheduler publishOnScheduler,
                                                                                 int maxConcurrency) {
    return processor -> publisher -> {
      Flux<Event> flux = from(publisher)
          .parallel(min(getRuntime().availableProcessors(), maxConcurrency))
          .runOn(createReactorScheduler(parallelScheduler, scheduler -> isTransactionActive()))
          .composeGroup(processor)
          .sequential();
      if (publishOnScheduler != null) {
        return flux.publishOn(createReactorScheduler(publishOnScheduler, scheduler -> isTransactionActive()));
      } else {
        return flux;
      }
    };
  }

  static reactor.core.scheduler.Scheduler createReactorScheduler(Scheduler scheduler) {
    return createReactorScheduler(scheduler, scheduler1 -> false);
  }


  static reactor.core.scheduler.Scheduler createReactorScheduler(Scheduler scheduler,
                                                                 Predicate<Scheduler> overrideSchedule) {
    return fromExecutorService(new ConditionalExecutorServiceDecorator(scheduler, overrideSchedule));
  }

}
