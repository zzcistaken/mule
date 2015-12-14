/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.firstextension;

import org.mule.extension.annotation.api.Parameter;

/**
 * Created by pablocabrera on 11/19/15.
 */
public class SimplePojo
{

    @Parameter
    private String someField;

    public String getSomeField()
    {
        return someField;
    }

    public void setSomeField(String someField)
    {
        this.someField = someField;
    }
}
