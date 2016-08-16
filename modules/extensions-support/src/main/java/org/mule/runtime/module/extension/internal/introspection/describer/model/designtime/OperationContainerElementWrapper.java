/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer.model.designtime;

import static javax.lang.model.util.ElementFilter.methodsIn;

import org.mule.runtime.module.extension.internal.introspection.describer.model.MethodElement;
import org.mule.runtime.module.extension.internal.introspection.describer.model.OperationContainerElement;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

public class OperationContainerElementWrapper extends TypeElementWrapper implements OperationContainerElement {


  private final ProcessingEnvironment environment;

  public OperationContainerElementWrapper(TypeMirror typeMirror, ProcessingEnvironment environment) {
    super(typeMirror, environment);
    this.environment = environment;
  }

  @Override
  public List<MethodElement> getOperations() {
    return methodsIn(element.getEnclosedElements())
        .stream()
        .map(method -> new MethodElementWrapper(method, environment))
        .collect(Collectors.toList());
  }
}
