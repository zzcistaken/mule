/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.runtime.source;

import static org.mule.runtime.module.extension.internal.util.IntrospectionUtils.checkInstantiable;

import org.mule.runtime.core.util.ClassUtils;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceFactory;

import java.util.function.Supplier;

public final class DefaultSourceFactory implements SourceFactory {

  private Supplier<Class<?>> sourceTypeName;
  private Class<? extends Source> sourceType;

  public DefaultSourceFactory(Class<? extends Source> sourceType) {
    checkInstantiable(sourceType);
    this.sourceType = sourceType;
  }

  public DefaultSourceFactory(Supplier<Class<?>> sourceType) {
    // checkInstantiable(sourceType);
    // this.sourceType = sourceType;
    this.sourceTypeName = sourceType;
  }

  @Override
  public Source createSource() {
    if (sourceType != null) {
      return getSource(sourceType);
    } else {
      return getSource(sourceTypeName.get());
    }
  }

  public Source getSource(Class aClass) {
    try {
      return (Source) ClassUtils.instanciateClass(aClass);
    } catch (Exception e) {
      throw new RuntimeException("Exception found trying to instantiate source type " + aClass.getName(), e);
    }
  }
}
