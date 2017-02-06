/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import static reactor.util.concurrent.QueueSupplier.SMALL_BUFFER_SIZE;

import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategyFactory;

/**
 * Common configuration for {@link ProcessingStrategyFactory}'s that need to configure the use of a cached IO pool.
 */
public interface BufferConfig
{

  int DEFAULT_BUFFER_SIZE = SMALL_BUFFER_SIZE;

  /**
   * Configure the size of the ring-buffer size used to buffer and de-multiplex events from multiple source threads. This value
   * must be a power-of two.
   * <p/>
   * Ring buffers typically use a power of two because it means that the rollover at the end of the buffer can be achieved using a
   * bit mask rather than having to explicitly compare the head/tail pointer with the end of the buffer.
   *
   * @param bufferSize buffer size to use.
   */
  void setBufferSize(int bufferSize);

}
