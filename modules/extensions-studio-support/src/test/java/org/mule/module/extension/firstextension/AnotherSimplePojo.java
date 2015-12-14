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
public class AnotherSimplePojo
{

    @Parameter
    private String anotherSimpleField;

    @Parameter
    private InnerPojo child;

    public String getAnotherSimpleField()
    {
        return anotherSimpleField;
    }

    public void setAnotherSimpleField(String anotherSimpleField)
    {
        this.anotherSimpleField = anotherSimpleField;
    }

    public InnerPojo getChild()
    {
        return child;
    }

    public void setChild(InnerPojo child)
    {
        this.child = child;
    }
}
