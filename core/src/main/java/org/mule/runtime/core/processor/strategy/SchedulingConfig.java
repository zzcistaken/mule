/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.processor.strategy;

import org.mule.runtime.core.api.processor.strategy.ProcessingStrategyFactory;

/**
 * Common configuration for {@link ProcessingStrategyFactory}'s that need to configure the use of a cached IO pool.
 */
interface SchedulingConfig {

  /**
   * Configures the maximum number of concurrent tasks that are permitted. This will typically be used to limit the number of
   * concurrent blocking tasks using the IO pool, but if set, will limit all processing, on any thread pools, to this maximum too.
   *
   * @param maxConcurrency
   */
  void setMaxConcurrency(int maxConcurrency);
}
