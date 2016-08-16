/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer.model.designtime;

import org.mule.runtime.module.extension.internal.introspection.describer.model.Type;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by estebanwasinger on 8/10/16.
 */
public class ElementBasedTypeFactory {

  public static Type toType(TypeMirror typeMirror) {

    if (typeMirror.getKind().equals(TypeKind.DECLARED)) {
      //return new ClassTypeElementWrapper((Type.ClassType) typeMirror);
    }
    return null;
  }

}
