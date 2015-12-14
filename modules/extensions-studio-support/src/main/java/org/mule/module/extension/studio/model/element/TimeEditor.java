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

@XmlRootElement(name = "time")
public class TimeEditor extends BaseFieldEditorElement
{

    private String max;
    private String min;
    private String defaultValue;
    private String step;

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getMax()
    {
        return max;
    }

    public void setMax(String max)
    {
        this.max = max;
    }

    @XmlAttribute
    public String getMin()
    {
        return min;
    }

    public void setMin(String min)
    {
        this.min = min;
    }

    @XmlAttribute
    public String getStep()
    {
        return step;
    }

    public void setStep(String step)
    {
        this.step = step;
    }

    @XmlAttribute
    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
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

        TimeEditor that = (TimeEditor) o;

        if (max != null ? !max.equals(that.max) : that.max != null)
        {
            return false;
        }
        if (min != null ? !min.equals(that.min) : that.min != null)
        {
            return false;
        }
        if (defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null)
        {
            return false;
        }
        return !(step != null ? !step.equals(that.step) : that.step != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (step != null ? step.hashCode() : 0);
        return result;
    }
}
