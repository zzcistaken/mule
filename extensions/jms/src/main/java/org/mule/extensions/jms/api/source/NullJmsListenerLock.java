/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.source;

import org.mule.runtime.api.message.Error;

public class NullJmsListenerLock implements JmsListenerLock {

  @Override
  public void lock() {

  }

  @Override
  public void unlockWithFailure(Error error) {

  }

  @Override
  public void unlock() {

  }

  @Override
  public boolean isLocked() {
    return false;
  }
}
