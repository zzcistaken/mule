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

@XmlRootElement(name = "label")
public class LabelElement extends BaseFieldEditorElement
{
    private Boolean alignCenter;

    @Override
    public String toString()
    {
        return "LabelElement [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getAlignCenter()
    {
        return alignCenter;
    }

    public void setAlignCenter(Boolean alignCenter)
    {
        this.alignCenter = alignCenter;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }

        LabelElement that = (LabelElement) o;

        return !(alignCenter != null ? !alignCenter.equals(that.alignCenter) : that.alignCenter != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (alignCenter != null ? alignCenter.hashCode() : 0);
        return result;
    }
}
