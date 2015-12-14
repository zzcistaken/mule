/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.firstextension;

import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.ParameterGroup;
import org.mule.extension.annotation.api.param.Optional;

public class ExtensionOperationsWithDefaults
{

    @Operation
    public String stringOperationWithDefault(String param1, @Optional(defaultValue = "fruta") String param2)
    {
        return param1 + " & " + param2;
    }

    @Operation
    public int primitiveTypesOperationWithDefault(int a, @Optional(defaultValue = "3") int b)
    {
        return a + b;
    }

    //TODO This doesn't make sense
    @Operation
    public SimplePojo parameterGroupPojoOperationWithDefault(@ParameterGroup SimplePojo pojo)
    {
        return pojo;
    }

    @Operation
    public SimplePojo pojoOperationWithDefault(@Optional(defaultValue = "#[payload]") SimplePojo pojo)
    {
        return pojo;
    }
}
