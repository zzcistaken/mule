/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static org.mule.runtime.core.processor.strategy.RingBufferConfig.WaitStrategy.LITE_BLOCKING;

import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategyFactory;

/**
 * Common configuration for {@link ProcessingStrategyFactory}'s that need to configure the use of a cached IO pool.
 */
public interface RingBufferConfig extends BufferConfig {

  WaitStrategy DEFAULT_WAIT_STRATEGY = LITE_BLOCKING;

  /**
   * Configure the number of ring-buffer subscribers.
   *
   * @param subscriberCount
   */
  void setSubscriberCount(int subscriberCount);

  /**
   * Configure the wait strategy used to wait for new events on ring-buffer.
   *
   * @param waitStrategy
   */
  void setWaitStrategy(WaitStrategy waitStrategy);

  /**
   * The strategy that should be used when waiting on {@link Event}'s on ring-buffer.
   */
  enum WaitStrategy {

    /**
     * Blocking strategy that uses a lock and condition variable for consumer waiting on a barrier.
     *
     * This strategy can be used when throughput and low-latency are not as important as CPU resource.
     */
    BLOCKING,

    /**
     * Variation of the {@link #BLOCKING} that attempts to elide conditional wake-ups when the lock is uncontended. Shows
     * performance improvements on micro-benchmarks. However this wait strategy should be considered experimental as I have not
     * full proved the correctness of the lock elision code.
     */
    LITE_BLOCKING,

    /**
     * Yielding strategy that uses a Thread.sleep(1) for consumers waiting on a barrier after an initially spinning.
     *
     * This strategy will incur up a latency of 1ms and save a maximum CPU resources.
     */
    SLEEPING,

    /**
     * Busy Spin strategy that uses a busy spin loop for consumers waiting on a barrier.
     *
     * This strategy will use CPU resource to avoid system calls which can introduce latency jitter. It is best used when threads
     * can be bound to specific CPU cores.
     */
    BUSY_SPIN,

    /**
     * Yielding strategy that uses a Thread.yield() for consumers waiting on a barrier after an initially spinning.
     *
     * This strategy is a good compromise between performance and CPU resource without incurring significant latency spikes.
     */
    YIELDING,

    /**
     * Parking strategy that initially spins, then uses a Thread.yield(), and eventually sleep
     * (<code>LockSupport.parkNanos(1)</code>) for the minimum number of nanos the OS and JVM will allow while the consumers are
     * waiting on a barrier.
     * <p>
     * This strategy is a good compromise between performance and CPU resource. Latency spikes can occur after quiet periods.
     */
    PARKING,

    /**
     * Phased wait strategy for waiting consumers on a barrier.
     * <p/>
     * This strategy can be used when throughput and low-latency are not as important as CPU resource. Spins, then yields, then
     * waits using {@link #BLOCKING} strategy.
     */
    PHASED

  }
}
