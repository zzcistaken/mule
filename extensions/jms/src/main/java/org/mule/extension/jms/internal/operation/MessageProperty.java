/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.operation;

import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.param.Optional;

public class MessageProperty
{
    @Parameter
    @Optional
    private String key;

    @Parameter
    @Optional
    private String value;

    @Parameter
    @Optional
    private String expression;

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

    public String getExpression()
    {
        return expression;
    }
}
