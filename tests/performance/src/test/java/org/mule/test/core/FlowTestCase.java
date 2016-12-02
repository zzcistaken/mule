/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.core;

import static java.util.Collections.singletonList;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.config.builders.BasicRuntimeServicesConfigurationBuilder;
import org.mule.runtime.core.DefaultEventContext;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.config.ConfigurationBuilder;
import org.mule.runtime.core.api.message.InternalMessage;
import org.mule.runtime.core.construct.Flow;
import org.mule.runtime.core.exception.MessagingException;
import org.mule.runtime.core.processor.strategy.MultiReactorProcessingStrategyFactory;
import org.mule.runtime.core.processor.strategy.ReactorProcessingStrategyFactory;
import org.mule.runtime.core.processor.strategy.RingBufferProcessingStrategyFactory;
import org.mule.runtime.core.processor.strategy.SynchronousProcessingStrategyFactory;
import org.mule.runtime.core.util.rx.Exceptions.EventDroppedException;
import org.mule.service.scheduler.internal.DefaultSchedulerService;
import org.mule.tck.TriggerableMessageSource;
import org.mule.tck.junit4.AbstractMuleContextTestCase;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import reactor.core.publisher.BlockingSink;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Mono;

/**
 * Test the performance of different approach of invoking a flow (blocking, mono, flux) along with the different processing
 * strategies.
 */
public class FlowTestCase extends AbstractMuleContextTestCase {

  private Flow defaultFlow;
  private Flow syncFlow;
  private Flow reactorFlow;
  private Flow multiReactorFlow;
  private Flow ringBufferFlow;
  private TriggerableMessageSource defaultSource;
  private TriggerableMessageSource syncSource;
  private TriggerableMessageSource reactorSource;
  private TriggerableMessageSource multiReactorSource;
  private TriggerableMessageSource ringBufferSource;
  private BlockingSink<Event> defaultSink;
  private BlockingSink<Event> syncSink;
  private BlockingSink<Event> reactorSink;
  private BlockingSink<Event> mutiReactorSink;
  private BlockingSink<Event> ringBufferSink;
  private static int ITERATIONS = 10000;

  @Rule
  public ContiPerfRule rule = new ContiPerfRule();

  protected void addBuilders(List<ConfigurationBuilder> builders) {
    builders.add(new BasicRuntimeServicesConfigurationBuilder());
  }

  @Override
  public int getTestTimeoutSecs() {
    return 600;
  }

  @Before
  public void setup() throws MuleException {
    muleContext.start();

    defaultSource = new TriggerableMessageSource();
    defaultFlow = new Flow("defaultFlow", muleContext);
    defaultFlow.setMessageProcessors(singletonList(event -> event));
    defaultFlow.setMessageSource(defaultSource);
    muleContext.getRegistry().registerFlowConstruct(defaultFlow);

    syncSource = new TriggerableMessageSource();
    syncFlow = new Flow("syncFlow", muleContext);
    syncFlow.setMessageProcessors(singletonList(event -> event));
    syncFlow.setMessageSource(syncSource);
    syncFlow.setProcessingStrategyFactory(new SynchronousProcessingStrategyFactory());
    muleContext.getRegistry().registerFlowConstruct(syncFlow);

    reactorSource = new TriggerableMessageSource();
    reactorFlow = new Flow("reactorFlow", muleContext);
    reactorFlow.setMessageProcessors(singletonList(event -> event));
    reactorFlow.setMessageSource(reactorSource);
    reactorFlow.setProcessingStrategyFactory(new ReactorProcessingStrategyFactory());
    muleContext.getRegistry().registerFlowConstruct(reactorFlow);

    multiReactorSource = new TriggerableMessageSource();
    multiReactorFlow = new Flow("multiReactorFlow", muleContext);
    multiReactorFlow.setMessageProcessors(singletonList(event -> event));
    multiReactorFlow.setMessageSource(multiReactorSource);
    multiReactorFlow.setProcessingStrategyFactory(new MultiReactorProcessingStrategyFactory());
    muleContext.getRegistry().registerFlowConstruct(multiReactorFlow);

    ringBufferSource = new TriggerableMessageSource();
    ringBufferFlow = new Flow("ringBufferFlow", muleContext);
    ringBufferFlow.setMessageProcessors(singletonList(event -> event));
    ringBufferFlow.setMessageSource(ringBufferSource);
    ringBufferFlow.setProcessingStrategyFactory(new RingBufferProcessingStrategyFactory());
    muleContext.getRegistry().registerFlowConstruct(ringBufferFlow);

    EmitterProcessor<Event> defaultEmitter = EmitterProcessor.create(1);
    defaultEmitter.transform(defaultSource)
        .doOnError(EventDroppedException.class, mde -> mde.getEvent().getContext().success())
        .doOnNext(response -> response.getContext().success(response))
        .doOnError(MessagingException.class, me -> me.getEvent().getContext().error(me))
        .subscribe();
    defaultSink = defaultEmitter.connectSink();

    EmitterProcessor<Event> syncEmitter = EmitterProcessor.create(1);
    syncEmitter.transform(syncSource)
        .doOnError(EventDroppedException.class, mde -> mde.getEvent().getContext().success())
        .doOnNext(response -> response.getContext().success(response))
        .doOnError(MessagingException.class, me -> me.getEvent().getContext().error(me))
        .subscribe();
    syncSink = syncEmitter.connectSink();

    EmitterProcessor<Event> reactorEmitter = EmitterProcessor.create(1);
    reactorEmitter.transform(reactorSource)
        .doOnError(EventDroppedException.class, mde -> mde.getEvent().getContext().success())
        .doOnNext(response -> response.getContext().success(response))
        .doOnError(MessagingException.class, me -> me.getEvent().getContext().error(me))
        .subscribe();
    reactorSink = reactorEmitter.connectSink();

    EmitterProcessor<Event> multiReactorEmitter = EmitterProcessor.create(1);
    multiReactorEmitter.transform(multiReactorSource)
        .doOnError(EventDroppedException.class, mde -> mde.getEvent().getContext().success())
        .doOnNext(response -> response.getContext().success(response))
        .doOnError(MessagingException.class, me -> me.getEvent().getContext().error(me))
        .subscribe();
    mutiReactorSink = multiReactorEmitter.connectSink();

    EmitterProcessor<Event> ringBufferEmitter = EmitterProcessor.create(1);
    ringBufferEmitter.transform(ringBufferSource)
        .doOnError(EventDroppedException.class, mde -> mde.getEvent().getContext().success())
        .doOnNext(response -> response.getContext().success(response))
        .doOnError(MessagingException.class, me -> me.getEvent().getContext().error(me))
        .subscribe();
    ringBufferSink = ringBufferEmitter.connectSink();
  }

  @After
  public void cleanup() throws MuleException {
    defaultSink.complete();
    syncSink.complete();
    reactorSink.complete();
    mutiReactorSink.complete();
    ringBufferSink.complete();
    ((DefaultSchedulerService) muleContext.getSchedulerService()).stop();
  }

  @Test
  @PerfTest(duration = 30000, threads = 1, warmUp = 10000)
  public void blockingDefaultProcessingStrategy() throws Exception {
    for (int i = 0; i < ITERATIONS; i++) {
      defaultSource.trigger(Event.builder(DefaultEventContext.create(defaultFlow, TEST_CONNECTOR))
          .message(InternalMessage.of(TEST_PAYLOAD)).build());
    }
  }

  @Test
  @PerfTest(duration = 30000, threads = 1, warmUp = 10000)
  public void blockingSyncProcessingStrategy() throws Exception {
    for (int i = 0; i < ITERATIONS; i++) {
      syncSource.trigger(Event.builder(DefaultEventContext.create(defaultFlow, TEST_CONNECTOR))
          .message(InternalMessage.of(TEST_PAYLOAD)).build());
    }
  }

  @Test
  @PerfTest(duration = 30000, threads = 1, warmUp = 10000)
  public void monoDefaultProcessingStrategy() throws Exception {
    for (int i = 0; i < ITERATIONS; i++) {
      Mono.just(Event.builder(DefaultEventContext.create(defaultFlow, TEST_CONNECTOR)).message(InternalMessage.of(TEST_PAYLOAD))
          .build()).transform(defaultSource).block();
    }
  }

  @Test
  @PerfTest(duration = 30000, threads = 1, warmUp = 10000)
  public void monoSyncProcessingStrategy() throws Exception {
    for (int i = 0; i < ITERATIONS; i++) {
      Mono.just(Event.builder(DefaultEventContext.create(defaultFlow, TEST_CONNECTOR)).message(InternalMessage.of(TEST_PAYLOAD))
          .build()).transform(syncSource).block();
    }
  }

  @Test
  @PerfTest(duration = 30000, threads = 1, warmUp = 10000)
  public void streamDefaultProcessingStrategy() throws Exception {
    CountDownLatch latch = new CountDownLatch(ITERATIONS);
    for (int i = 0; i < ITERATIONS; i++) {
      Event event = Event.builder(DefaultEventContext.create(defaultFlow, TEST_CONNECTOR))
          .message(InternalMessage.of(TEST_PAYLOAD)).build();
      defaultSink.accept(event);
      Mono.from(event.getContext()).doOnNext(e -> latch.countDown()).subscribe();
    }
    latch.await();
  }

  @Test
  @PerfTest(duration = 30000, threads = 1, warmUp = 10000)
  public void streamSyncProcessingStrategy() throws Exception {
    CountDownLatch latch = new CountDownLatch(ITERATIONS);
    for (int i = 0; i < ITERATIONS; i++) {
      Event event = Event.builder(DefaultEventContext.create(defaultFlow, TEST_CONNECTOR))
          .message(InternalMessage.of(TEST_PAYLOAD)).build();
      syncSink.accept(event);
      Mono.from(event.getContext()).doOnNext(e -> latch.countDown()).subscribe();
    }
    latch.await();
  }

  @Test
  @PerfTest(duration = 30000, threads = 1, warmUp = 10000)
  public void streamReactorProcessingStrategy() throws Exception {
    CountDownLatch latch = new CountDownLatch(ITERATIONS);
    for (int i = 0; i < ITERATIONS; i++) {
      Event event = Event.builder(DefaultEventContext.create(defaultFlow, TEST_CONNECTOR))
          .message(InternalMessage.of(TEST_PAYLOAD)).build();
      reactorSink.accept(event);
      Mono.from(event.getContext()).doOnNext(e -> latch.countDown()).subscribe();
    }
    latch.await();
  }

  @Test
  @PerfTest(duration = 30000, threads = 1, warmUp = 10000)
  public void streamMultiReactorProcessingStrategy() throws Exception {
    CountDownLatch latch = new CountDownLatch(ITERATIONS);
    for (int i = 0; i < ITERATIONS; i++) {
      Event event = Event.builder(DefaultEventContext.create(defaultFlow, TEST_CONNECTOR))
          .message(InternalMessage.of(TEST_PAYLOAD)).build();
      mutiReactorSink.accept(event);
      Mono.from(event.getContext()).doOnNext(e -> latch.countDown()).subscribe();
    }
    latch.await();
  }

  @Test
  @PerfTest(duration = 30000, threads = 1, warmUp = 10000)
  public void streamRingBufferProcessingStrategy() throws Exception {
    CountDownLatch latch = new CountDownLatch(ITERATIONS);
    for (int i = 0; i < ITERATIONS; i++) {
      Event event = Event.builder(DefaultEventContext.create(defaultFlow, TEST_CONNECTOR))
          .message(InternalMessage.of(TEST_PAYLOAD)).build();
      ringBufferSink.accept(event);
      Mono.from(event.getContext()).doOnNext(e -> latch.countDown()).subscribe();
    }
    latch.await();
  }

}
