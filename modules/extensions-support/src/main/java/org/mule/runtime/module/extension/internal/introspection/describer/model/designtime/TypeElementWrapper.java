/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer.model.designtime;

import org.mule.runtime.module.extension.internal.introspection.describer.model.FieldElement;
import org.mule.runtime.module.extension.internal.introspection.describer.model.Type;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import static java.util.Collections.emptyList;
import static org.mule.runtime.module.extension.internal.introspection.describer.model.designtime.ASTUtils.isJavaType;

public class TypeElementWrapper implements Type {

  final TypeElement element;
  private final TypeMirror typeMirror;
  private final ProcessingEnvironment environment;
  private List<FieldElement> fields = emptyList();

  public TypeElementWrapper(TypeMirror typeMirror, ProcessingEnvironment environment) {
    this.typeMirror = typeMirror;
    this.environment = environment;
    element = (TypeElement) environment.getTypeUtils().asElement(typeMirror);
    if (typeMirror.getKind() == TypeKind.DECLARED && !isJavaType(element)) {
      fields = ElementFilter.fieldsIn(element.getEnclosedElements())
          .stream()
          .map(field -> new FieldElementWrapper(field, environment))
          .collect(Collectors.toList());
    }
  }

  @Override
  public String getName() {
    final String[] split = typeMirror.toString().split("\\.");
    return split[split.length - 1];
  }

  @Override
  public List<FieldElement> getFields() {
    return fields;
  }

  @Override
  public Class getDeclaredClass() {
    return null;
  }

  @Override
  public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass) {
    return Optional.ofNullable(typeMirror.getAnnotation(annotationClass));
  }

  @Override
  public String getClassName() {
    return typeMirror.toString();
  }

  @Override
  public Supplier<Class<?>> getClassSupplier() {
    return new ClassNameBasedSupplier(getClassName());
  }

  @Override
  public boolean isAssignableFrom(Type type) {
    final TypeMirror toCompareTypeMirror = environment.getElementUtils().getTypeElement(type.getClassName()).asType();
    return environment.getTypeUtils().isAssignable(toCompareTypeMirror, typeMirror);
  }

  @Override
  public boolean isAssignableTo(Type type) {
    final DeclaredType declaredType =
        environment.getTypeUtils().getDeclaredType(environment.getElementUtils().getTypeElement(type.getClassName()));
    return environment.getTypeUtils().isAssignable(typeMirror, declaredType);
  }
}
