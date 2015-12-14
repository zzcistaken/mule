/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element;

import org.mule.module.extension.studio.model.IEditorElementVisitor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by pablocabrera on 11/18/15.
 */
@XmlRootElement(name = "datetime")
public class DateTimeEditor extends BaseFieldEditorElement
{

    private InputType inputType;
    private String outputFormat;

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public InputType getInputType()
    {
        return inputType;
    }

    public void setInputType(InputType inputType)
    {
        this.inputType = inputType;
    }


    @XmlAttribute
    public String getOutputFormat()
    {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat)
    {
        this.outputFormat = outputFormat;
    }
}
