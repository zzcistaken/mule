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

@XmlRootElement(name = "double")
public class DoubleEditor extends BaseFieldEditorElement
{

    private Double min;
    private Double max;
    private Double step;
    private Double defaultValue;

    @XmlAttribute
    public Double getMin()
    {
        return min;
    }

    public void setMin(Double min)
    {
        this.min = min;
    }

    @XmlAttribute
    public Double getMax()
    {
        return max;
    }

    public void setMax(Double max)
    {
        this.max = max;
    }

    @XmlAttribute
    public Double getStep()
    {
        return step;
    }

    public void setStep(Double step)
    {
        this.step = step;
    }

    @XmlAttribute
    public Double getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(Double defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString()
    {
        return "DoubleEditor [min=" + min + ", max=" + max + ", step=" + step + ", defaultValue=" + defaultValue + ", getName()=" + getName() + ", getCaption()=" + getCaption()
               + "]";
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((max == null) ? 0 : max.hashCode());
        result = prime * result + ((min == null) ? 0 : min.hashCode());
        result = prime * result + ((step == null) ? 0 : step.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        DoubleEditor other = (DoubleEditor) obj;
        if (defaultValue == null)
        {
            if (other.defaultValue != null)
            {
                return false;
            }
        }
        else if (!defaultValue.equals(other.defaultValue))
        {
            return false;
        }
        if (max == null)
        {
            if (other.max != null)
            {
                return false;
            }
        }
        else if (!max.equals(other.max))
        {
            return false;
        }
        if (min == null)
        {
            if (other.min != null)
            {
                return false;
            }
        }
        else if (!min.equals(other.min))
        {
            return false;
        }
        if (step == null)
        {
            if (other.step != null)
            {
                return false;
            }
        }
        else if (!step.equals(other.step))
        {
            return false;
        }
        return true;
    }
}
