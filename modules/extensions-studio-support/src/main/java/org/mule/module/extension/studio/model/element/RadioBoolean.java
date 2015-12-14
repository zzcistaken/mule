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

@XmlRootElement
public class RadioBoolean extends BaseFieldEditorElement
{

    private String group;
    private Boolean defaultValue;
    private Integer margin;
    //@ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.editors.ILoadedValueModifier.class)
    private String loadedValueModifier;
    private String labelledWith;

    @Override
    public String toString()
    {
        return "RadioBoolean [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    @XmlAttribute
    public Boolean getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(Boolean defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @XmlAttribute
    public Integer getMargin()
    {
        return margin;
    }

    public void setMargin(Integer margin)
    {
        this.margin = margin;
    }

    @XmlAttribute
    public String getLoadedValueModifier()
    {
        return loadedValueModifier;
    }

    public void setLoadedValueModifier(String loadedValueModifier)
    {
        this.loadedValueModifier = loadedValueModifier;
    }

    @XmlAttribute
    public String getLabelledWith()
    {
        return labelledWith;
    }

    public void setLabelledWith(String labelledWith)
    {
        this.labelledWith = labelledWith;
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

        RadioBoolean that = (RadioBoolean) o;

        if (group != null ? !group.equals(that.group) : that.group != null)
        {
            return false;
        }
        if (defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null)
        {
            return false;
        }
        if (margin != null ? !margin.equals(that.margin) : that.margin != null)
        {
            return false;
        }
        if (loadedValueModifier != null ? !loadedValueModifier.equals(that.loadedValueModifier) : that.loadedValueModifier != null)
        {
            return false;
        }
        return !(labelledWith != null ? !labelledWith.equals(that.labelledWith) : that.labelledWith != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (margin != null ? margin.hashCode() : 0);
        result = 31 * result + (loadedValueModifier != null ? loadedValueModifier.hashCode() : 0);
        result = 31 * result + (labelledWith != null ? labelledWith.hashCode() : 0);
        return result;
    }
}
