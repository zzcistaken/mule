/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.source;

import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.api.message.Error;

import java.util.concurrent.Semaphore;

/**
 * //TODO JAVADOC
 */
public class DefaultJmsListenerLock implements JmsListenerLock {

  private Semaphore semaphore = new Semaphore(0);
  private boolean isFailure = false;
  private Throwable cause;

  @Override
  public void lock() {
    semaphore.acquireUninterruptibly();
    if (isFailure) {
      throw new MuleRuntimeException(cause);
    }
  }

  @Override
  public void unlockWithFailure(Error error) {
    this.isFailure = true;
    cause = error.getCause();
    semaphore.release();
  }

  @Override
  public void unlock() {
    this.isFailure = false;
    semaphore.release();
  }

  @Override
  public boolean isLocked() {
    return semaphore.hasQueuedThreads();
  }
}
