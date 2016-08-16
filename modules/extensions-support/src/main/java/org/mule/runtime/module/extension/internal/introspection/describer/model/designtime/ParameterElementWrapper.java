/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer.model.designtime;

import org.mule.metadata.api.ClassTypeLoader;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.module.extension.internal.introspection.describer.model.ParameterElement;
import org.mule.runtime.module.extension.internal.introspection.describer.model.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;

import static org.mule.runtime.module.extension.internal.introspection.describer.AnnotationsBasedDescriber.STRING_TYPE;

public class ParameterElementWrapper implements ParameterElement {

  private final VariableElement param;
  private final ProcessingEnvironment environment;

  public ParameterElementWrapper(VariableElement param, ProcessingEnvironment environment) {

    this.param = param;
    this.environment = environment;
  }

  @Override
  public String getOwnerDescription() {
    return null;
  }

  @Override
  public Parameter getParameter() {
    return null;
  }

  @Override
  public String getName() {
    return param.getSimpleName().toString();
  }

  @Override
  public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass) {
    return Optional.ofNullable(param.getAnnotation(annotationClass));
  }

  public MetadataType getMetadataType(ClassTypeLoader typeLoader) {
    return STRING_TYPE;
  }

  @Override
  public Type getType() {
    return new TypeElementWrapper(param.asType(), environment);
  }
}
