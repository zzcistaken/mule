/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer.model.designtime;

import com.google.common.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

public class ASTUtils {

  public static <T> Optional<T> findAnnotationValue(Element element, String annotationClass,
                                                    String valueName, TypeToken<T> typeToken) {
    T ret = null;
    for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
      DeclaredType annotationType = annotationMirror.getAnnotationType();
      TypeElement annotationElement = (TypeElement) annotationType.asElement();
      if (annotationElement.getQualifiedName().contentEquals(annotationClass)) {
        ret = extractValue(annotationMirror, valueName, typeToken);
        break;
      }
    }
    return Optional.ofNullable(ret);
  }

  public static <T> T extractValue(AnnotationMirror annotationMirror, String valueName, TypeToken<T> typeToken) {
    Map<ExecutableElement, AnnotationValue> elementValues = new HashMap<>(annotationMirror.getElementValues());
    for (Map.Entry<ExecutableElement, AnnotationValue> entry : elementValues.entrySet()) {
      if (entry.getKey().getSimpleName().contentEquals(valueName)) {
        return (T) entry.getValue().getValue();
      }
    }
    return null;
  }

  public static boolean isJavaType(Element element) {
    return (element.asType().toString().startsWith("java")
        || element.asType().toString().startsWith("sun"));
  }
}
