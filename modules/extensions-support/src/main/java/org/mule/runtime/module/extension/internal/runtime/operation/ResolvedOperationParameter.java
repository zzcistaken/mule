/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.runtime.operation;

public class ResolvedOperationParameter
{

    private OperationParameter operationParameter;
    private Object value;

    public ResolvedOperationParameter(OperationParameter operationParameter, Object value)
    {
        this.operationParameter = operationParameter;
        this.value = value;
    }

    public OperationParameter getOperationParameter()
    {
        return operationParameter;
    }

    public Object getValue()
    {
        return value;
    }
}
