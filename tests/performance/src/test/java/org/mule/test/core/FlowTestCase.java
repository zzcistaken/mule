/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.core;

import static java.util.Collections.singletonList;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.config.builders.BasicRuntimeServicesConfigurationBuilder;
import org.mule.runtime.core.DefaultEventContext;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.config.ConfigurationBuilder;
import org.mule.runtime.core.api.message.InternalMessage;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.construct.Flow;
import org.mule.runtime.core.exception.MessagingException;
import org.mule.runtime.core.processor.strategy.ReactorProcessingStrategyFactory;
import org.mule.runtime.core.processor.strategy.RingBufferReactorProcessingStrategyFactory;
import org.mule.runtime.core.processor.strategy.SynchronousProcessingStrategyFactory;
import org.mule.runtime.core.util.rx.Exceptions.EventDroppedException;
import org.mule.tck.TriggerableMessageSource;
import org.mule.tck.config.TestServicesConfigurationBuilder;
import org.mule.tck.junit4.AbstractMuleContextTestCase;
import org.mule.test.performance.util.AbstractIsolatedFunctionalPerformanceTestCase;

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
 *
 */
public class FlowTestCase extends AbstractMuleContextTestCase {


  private Flow defaultFlow;
  private Flow syncFlow;
  private Flow reactorFlow;
  private Flow ringBufferFlow;

  private TriggerableMessageSource defaultSource;
  private TriggerableMessageSource syncSource;
  private TriggerableMessageSource reactorSource;
  private TriggerableMessageSource ringBufferSource;
  private BlockingSink<Event> defaultFluxSnk;
  private BlockingSink<Event> syncFluxSnk;
  private BlockingSink<Event> reactorFluxSink;
  private BlockingSink<Event> ringBufferFluxSink;
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

    ringBufferSource = new TriggerableMessageSource();
    ringBufferFlow = new Flow("ringBufferFlow", muleContext);
    ringBufferFlow.setMessageProcessors(singletonList(event -> event));
    ringBufferFlow.setMessageSource(ringBufferSource);
    ringBufferFlow.setProcessingStrategyFactory(new RingBufferReactorProcessingStrategyFactory());
    muleContext.getRegistry().registerFlowConstruct(reactorFlow);


    DirectProcessor<Event> emitterProcessor = DirectProcessor.create();
    emitterProcessor.transform(defaultSource)
        .doOnError(EventDroppedException.class, mde -> mde.getEvent().getContext().success())
        .doOnNext(response -> response.getContext().success(response))
        .doOnError(MessagingException.class, me -> me.getEvent().getContext().error(me))
        .subscribe();
    defaultFluxSnk = emitterProcessor.connectSink();

    DirectProcessor<Event> emitterProcessor2 = DirectProcessor.create();
    emitterProcessor2.transform(syncSource)
        .doOnError(EventDroppedException.class, mde -> mde.getEvent().getContext().success())
        .doOnNext(response -> response.getContext().success(response))
        .doOnError(MessagingException.class, me -> me.getEvent().getContext().error(me))
        .subscribe();
    syncFluxSnk = emitterProcessor2.connectSink();

    DirectProcessor<Event> emitterProcessor3 = DirectProcessor.create();
    emitterProcessor3.transform(syncSource)
        .doOnError(EventDroppedException.class, mde -> mde.getEvent().getContext().success())
        .doOnNext(response -> response.getContext().success(response))
        .doOnError(MessagingException.class, me -> me.getEvent().getContext().error(me))
        .subscribe();
    reactorFluxSink = emitterProcessor3.connectSink();

    DirectProcessor<Event> emitterProcessor4 = DirectProcessor.create();
    emitterProcessor4.transform(syncSource)
        .doOnError(EventDroppedException.class, mde -> mde.getEvent().getContext().success())
        .doOnNext(response -> response.getContext().success(response))
        .doOnError(MessagingException.class, me -> me.getEvent().getContext().error(me))
        .subscribe();
    ringBufferFluxSink = emitterProcessor4.connectSink();

  }

  @After
  public void cleanup() {
    defaultFluxSnk.complete();
    syncFluxSnk.complete();
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
      defaultFluxSnk.accept(event);
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
      syncFluxSnk.accept(event);
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
      reactorFluxSink.accept(event);
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
      ringBufferFluxSink.accept(event);
      Mono.from(event.getContext()).doOnNext(e -> latch.countDown()).subscribe();
    }
    latch.await();
  }


}
