/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.reference;

import org.mule.module.extension.studio.model.IEditorElementVisitor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GlobalRef extends AbstractRef
{

    private String attrName;
    private String requiredType;
    private String additionalCheckbox;
    private Boolean hideToolBar;// TODO should this be in the parent...it is only used here...but...
    private String listRequiredType;

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getRequiredType()
    {
        return requiredType;
    }

    public void setRequiredType(String requiredType)
    {
        this.requiredType = requiredType;
    }

    @XmlAttribute
    public String getAdditionalCheckbox()
    {
        return additionalCheckbox;
    }

    public void setAdditionalCheckbox(String additionalCheckbox)
    {
        this.additionalCheckbox = additionalCheckbox;
    }

    @XmlAttribute
    public Boolean getHideToolBar()
    {
        return hideToolBar;
    }

    public void setHideToolBar(Boolean hideToolBar)
    {
        this.hideToolBar = hideToolBar;
    }

    @XmlAttribute
    public String getAttrName()
    {
        return attrName;
    }

    public void setAttrName(String attrName)
    {
        this.attrName = attrName;
    }

    @XmlAttribute
    public String getListRequiredType()
    {
        return listRequiredType;
    }

    public void setListRequiredType(String listRequiredType)
    {
        this.listRequiredType = listRequiredType;
    }

    @Override
    public String toString()
    {
        return "GlobalRef{" +
               "attrName='" + attrName + '\'' +
               ", requiredType='" + requiredType + '\'' +
               ", additionalCheckbox='" + additionalCheckbox + '\'' +
               ", hideToolBar=" + hideToolBar +
               ", listRequiredType='" + listRequiredType + '\'' +
               '}';
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

        GlobalRef globalRef = (GlobalRef) o;

        if (attrName != null ? !attrName.equals(globalRef.attrName) : globalRef.attrName != null)
        {
            return false;
        }
        if (requiredType != null ? !requiredType.equals(globalRef.requiredType) : globalRef.requiredType != null)
        {
            return false;
        }
        if (additionalCheckbox != null ? !additionalCheckbox.equals(globalRef.additionalCheckbox) : globalRef.additionalCheckbox != null)
        {
            return false;
        }
        if (hideToolBar != null ? !hideToolBar.equals(globalRef.hideToolBar) : globalRef.hideToolBar != null)
        {
            return false;
        }
        return !(listRequiredType != null ? !listRequiredType.equals(globalRef.listRequiredType) : globalRef.listRequiredType != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (attrName != null ? attrName.hashCode() : 0);
        result = 31 * result + (requiredType != null ? requiredType.hashCode() : 0);
        result = 31 * result + (additionalCheckbox != null ? additionalCheckbox.hashCode() : 0);
        result = 31 * result + (hideToolBar != null ? hideToolBar.hashCode() : 0);
        result = 31 * result + (listRequiredType != null ? listRequiredType.hashCode() : 0);
        return result;
    }
}
