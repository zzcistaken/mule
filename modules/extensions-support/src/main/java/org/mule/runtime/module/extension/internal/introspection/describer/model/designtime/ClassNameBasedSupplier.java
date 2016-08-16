/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer.model.designtime;

import java.util.function.Supplier;

public class ClassNameBasedSupplier implements Supplier<Class<?>> {

  private final String className;

  public ClassNameBasedSupplier(String className) {

    this.className = className;
  }

  @Override
  public Class<?> get() {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Cannot find class");
    }
  }
}
