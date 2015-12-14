/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.module.extension.studio.model.IEditorElementVisitor;

@XmlRootElement(name = "name")
public class NameEditor extends BaseFieldEditorElement
{

    private String defaultValue;
    // TODO xq no usar el required??
    private Boolean appearsAsRequired;

    @Override
    public String toString()
    {
        return "NameEditor [getName()=" + getName() + ", getCaption()=" + getCaption() + ", getDescription()=" + getDescription() + "]";
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getAppearsAsRequired()
    {
        return appearsAsRequired;
    }

    public void setAppearsAsRequired(Boolean appearsAsRequired)
    {
        this.appearsAsRequired = appearsAsRequired;
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
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((appearsAsRequired == null) ? 0 : appearsAsRequired.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
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
        NameEditor other = (NameEditor) obj;
        if (appearsAsRequired == null)
        {
            if (other.appearsAsRequired != null)
            {
                return false;
            }
        }
        else if (!appearsAsRequired.equals(other.appearsAsRequired))
        {
            return false;
        }
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
        return true;
    }
}
