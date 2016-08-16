/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer.model.designtime;

import com.google.common.reflect.TypeToken;

import com.sun.tools.javac.code.Attribute;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.connector.Providers;
import org.mule.runtime.module.extension.internal.introspection.describer.model.ComponentElement;
import org.mule.runtime.module.extension.internal.introspection.describer.model.ConnectionProviderElement;
import org.mule.runtime.module.extension.internal.introspection.describer.model.OperationContainerElement;
import org.mule.runtime.module.extension.internal.introspection.describer.model.SourceElement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

import static java.util.Collections.emptyList;

public class ComponentElementWrapper extends TypeElementWrapper implements ComponentElement {


  private final ProcessingEnvironment environment;

  public ComponentElementWrapper(TypeMirror typeMirror, ProcessingEnvironment environment) {
    super(typeMirror, environment);
    this.environment = environment;
  }

  @Override
  public List<SourceElement> getSources() {
    final Optional<List<Attribute.Class>> value = ASTUtils.findAnnotationValue(element, Sources.class.getName(), "value",
                                                                               new TypeToken<List<Attribute.Class>>() {});
    return value.isPresent() ? value.get()
        .stream().map(aClass -> new SourceElementWrapper(aClass.getValue(), environment))
        .collect(Collectors.toList()) : emptyList();
  }

  @Override
  public List<OperationContainerElement> getOperationContainers() {
    final Optional<List<Attribute.Class>> value = ASTUtils.findAnnotationValue(element, Operations.class.getName(), "value",
                                                                               new TypeToken<List<Attribute.Class>>() {});
    return value.isPresent() ? value.get()
        .stream().map(aClass -> new OperationContainerElementWrapper(aClass.getValue(), environment))
        .collect(Collectors.toList()) : emptyList();
  }

  @Override
  public List<ConnectionProviderElement> getConnectionProviders() {

    final Optional<List<Attribute.Class>> value = ASTUtils.findAnnotationValue(element, Providers.class.getName(), "value",
                                                                               new TypeToken<List<Attribute.Class>>() {});
    return value.isPresent() ? value.get()
        .stream().map(aClass -> new ConnectionProviderElementWrapper(aClass.getValue(), environment))
        .collect(Collectors.toList()) : emptyList();
  }
}
