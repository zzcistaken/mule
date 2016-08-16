/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer.model.designtime;

import static java.util.Collections.emptyList;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.module.extension.internal.introspection.describer.model.ConfigurationElement;
import org.mule.runtime.module.extension.internal.introspection.describer.model.ExtensionElement;
import org.mule.runtime.module.extension.internal.introspection.describer.model.ParameterizableTypeElement;

import com.google.common.reflect.TypeToken;
import com.sun.tools.javac.code.Attribute;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

public class ExtensionElementWrapper extends ComponentElementWrapper implements ExtensionElement, ParameterizableTypeElement {

  private final ProcessingEnvironment processingEnv;
  private Extension extensionAnnotation;

  public ExtensionElementWrapper(TypeMirror typeMirror, ProcessingEnvironment processingEnv) {
    super(typeMirror, processingEnv);
    this.processingEnv = processingEnv;
    extensionAnnotation = element.getAnnotation(Extension.class);
  }

  @Override
  public String getName() {
    return extensionAnnotation.name();
  }


  @Override
  public List<ConfigurationElement> getConfigurations() {
    final Optional<List<Attribute.Class>> value = ASTUtils.findAnnotationValue(element, Configurations.class.getName(), "value",
                                                                               new TypeToken<List<Attribute.Class>>() {});
    return value.isPresent() ? value.get()
        .stream().map(aClass -> new ConfigurationElementWrapper(aClass.getValue(), processingEnv))
        .collect(Collectors.toList()) : emptyList();
  }
}
