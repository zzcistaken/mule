/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer.model.designtime;

import org.mule.runtime.module.extension.internal.introspection.describer.model.SourceElement;

import com.sun.tools.javac.code.Type;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;

public class SourceElementWrapper extends TypeElementWrapper implements SourceElement {

  public SourceElementWrapper(Type value, ProcessingEnvironment environment) {
    super(value, environment);
  }

  @Override
  public List<java.lang.reflect.Type> getSuperClassGenerics() {
    return null;
  }
}
