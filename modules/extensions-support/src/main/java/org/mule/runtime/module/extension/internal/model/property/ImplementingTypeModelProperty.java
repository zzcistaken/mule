/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.model.property;

import org.mule.runtime.extension.api.introspection.EnrichableModel;
import org.mule.runtime.extension.api.introspection.ModelProperty;
import org.mule.runtime.module.extension.internal.introspection.describer.model.Type;

import java.util.function.Supplier;

/**
 * An immutable model property which indicates that the owning {@link EnrichableModel} was derived from a given {@link #type}
 *
 * @since 4.0
 */
public final class ImplementingTypeModelProperty implements ModelProperty {

  private Supplier<Class<?>> classSupplier;
  private Class<?> type;
  private String className;
  private Type typeElement;

  /**
   * Creates a new instance referencing the given {@code type}
   *
   * @param type a {@link Class} which defines the owning {@link EnrichableModel}
   */
  public ImplementingTypeModelProperty(Class<?> type) {
    this.type = type;
  }

  public ImplementingTypeModelProperty(Type type) {

    typeElement = type;
  }

  /**
   * @return a {@link Class} which defines the owning {@link EnrichableModel}
   */
  public Class<?> getType() {
    return typeElement.getClassSupplier().get();
  }

  public Type getTypeEle() {
    return typeElement;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code implementingType}
   */
  @Override
  public String getName() {
    return "implementingType";
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code false}
   */
  @Override
  public boolean isExternalizable() {
    return false;
  }
}
