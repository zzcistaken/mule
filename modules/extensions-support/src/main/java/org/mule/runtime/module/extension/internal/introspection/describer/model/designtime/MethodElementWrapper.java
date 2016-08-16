/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer.model.designtime;

import static java.util.stream.Collectors.toList;

import org.mule.runtime.extension.api.annotation.ParameterGroup;
import org.mule.runtime.module.extension.internal.introspection.describer.model.ExtensionParameter;
import org.mule.runtime.module.extension.internal.introspection.describer.model.MethodElement;
import org.mule.runtime.module.extension.internal.introspection.describer.model.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;

public class MethodElementWrapper implements MethodElement {

  private final ExecutableElement element;
  private final ProcessingEnvironment processingEnvironment;

  public MethodElementWrapper(ExecutableElement element, ProcessingEnvironment processingEnvironment) {
    this.element = element;
    this.processingEnvironment = processingEnvironment;
  }

  @Override
  public Class<?> getReturnType() {
    return null;
  }

  @Override
  public Type getReturnTypeWrapper() {
    return new TypeElementWrapper(element.getReturnType(), processingEnvironment);
  }

  @Override
  public String getName() {
    return element.getSimpleName().toString();
  }

  @Override
  public List<ExtensionParameter> getParameters() {
    return element.getParameters().stream().map(param -> new ParameterElementWrapper(param, processingEnvironment))
        .collect(toList());
  }

  @Override
  public List<ExtensionParameter> getParameterGroups() {
    return getParameters().stream().filter(param -> param.isAnnotatedWith(ParameterGroup.class)).collect(toList());
  }

  @Override
  public List<ExtensionParameter> getParametersAnnotatedWith(Class<? extends Annotation> annotationClass) {
    return getParameters().stream().filter(param -> param.isAnnotatedWith(annotationClass)).collect(toList());
  }

  @Override
  public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass) {
    return Optional.ofNullable(element.getAnnotation(annotationClass));
  }

  @Override
  public Method getMethod() {
    return null;
  }
}
