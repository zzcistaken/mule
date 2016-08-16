/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer.model.designtime;

import org.mule.runtime.module.extension.internal.introspection.describer.model.ConfigurationElement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

public class ConfigurationElementWrapper extends ComponentElementWrapper implements ConfigurationElement {

  public ConfigurationElementWrapper(TypeMirror type, ProcessingEnvironment processingEnvironment) {
    super(type, processingEnvironment);
  }
}
