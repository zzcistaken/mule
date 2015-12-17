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
public class ChildElement extends BaseFieldEditorElement
{

    private Boolean allowMultiple;
    private String additionalPriorities;
    // TODO este elemento esta en otra jerarquia....habra que mover la clase??
    private String localId;
    private Boolean tableUI;
    private Boolean removeBorder;
    private String groupLabel;
    private Integer xmlOrder;
    private Boolean inplace;
    private Boolean positional;
    private String editorsIds;
    private String allowedSubTypes;
    private Boolean allowSubTypes;

    @Override
    public String toString()
    {
        return "ChildElement [getName()=" + getName() + ", getCaption()=" + getCaption() + ", getDescription()=" + getDescription() + "]";
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getLocalId()
    {
        return localId;
    }

    public void setLocalId(String localId)
    {
        this.localId = localId;
    }

    @XmlAttribute
    public Boolean getTableUI()
    {
        return tableUI;
    }

    public void setTableUI(Boolean tableUI)
    {
        this.tableUI = tableUI;
    }

    @XmlAttribute
    public Boolean getRemoveBorder()
    {
        return removeBorder;
    }

    public void setRemoveBorder(Boolean removeBorder)
    {
        this.removeBorder = removeBorder;
    }

    @XmlAttribute
    public String getGroupLabel()
    {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel)
    {
        this.groupLabel = groupLabel;
    }

    @XmlAttribute
    public Integer getXmlOrder()
    {
        return xmlOrder;
    }

    public void setXmlOrder(Integer xmlOrder)
    {
        this.xmlOrder = xmlOrder;
    }

    @XmlAttribute
    public Boolean getInplace()
    {
        return inplace;
    }

    public void setInplace(Boolean inplace)
    {
        this.inplace = inplace;
    }

    @XmlAttribute
    public String getAdditionalPriorities()
    {
        return additionalPriorities;
    }

    public void setAdditionalPriorities(String additionalPriorities)
    {
        this.additionalPriorities = additionalPriorities;
    }

    @XmlAttribute
    public String getAllowedSubTypes()
    {
        return allowedSubTypes;
    }

    public void setAllowedSubTypes(String allowedSubTypes)
    {
        this.allowedSubTypes = allowedSubTypes;
    }

    @XmlAttribute
    public Boolean getAllowMultiple()
    {
        return allowMultiple;
    }

    public void setAllowMultiple(Boolean allowMultiple)
    {
        this.allowMultiple = allowMultiple;
    }

    @XmlAttribute
    public Boolean getAllowSubTypes()
    {
        return allowSubTypes;
    }

    public void setAllowSubTypes(Boolean allowSubTypes)
    {
        this.allowSubTypes = allowSubTypes;
    }

    @XmlAttribute
    public Boolean getPositional()
    {
        return positional;
    }

    public void setPositional(Boolean positional)
    {
        this.positional = positional;
    }

    @XmlAttribute
    public String getEditorsIds()
    {
        return editorsIds;
    }

    public void setEditorsIds(String editorsIds)
    {
        this.editorsIds = editorsIds;
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

        ChildElement that = (ChildElement) o;

        if (allowMultiple != null ? !allowMultiple.equals(that.allowMultiple) : that.allowMultiple != null)
        {
            return false;
        }
        if (additionalPriorities != null ? !additionalPriorities.equals(that.additionalPriorities) : that.additionalPriorities != null)
        {
            return false;
        }
        if (localId != null ? !localId.equals(that.localId) : that.localId != null)
        {
            return false;
        }
        if (tableUI != null ? !tableUI.equals(that.tableUI) : that.tableUI != null)
        {
            return false;
        }
        if (removeBorder != null ? !removeBorder.equals(that.removeBorder) : that.removeBorder != null)
        {
            return false;
        }
        if (groupLabel != null ? !groupLabel.equals(that.groupLabel) : that.groupLabel != null)
        {
            return false;
        }
        if (xmlOrder != null ? !xmlOrder.equals(that.xmlOrder) : that.xmlOrder != null)
        {
            return false;
        }
        if (inplace != null ? !inplace.equals(that.inplace) : that.inplace != null)
        {
            return false;
        }
        if (positional != null ? !positional.equals(that.positional) : that.positional != null)
        {
            return false;
        }
        if (editorsIds != null ? !editorsIds.equals(that.editorsIds) : that.editorsIds != null)
        {
            return false;
        }
        if (allowedSubTypes != null ? !allowedSubTypes.equals(that.allowedSubTypes) : that.allowedSubTypes != null)
        {
            return false;
        }
        return allowSubTypes != null ? allowSubTypes.equals(that.allowSubTypes) : that.allowSubTypes == null;

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (allowMultiple != null ? allowMultiple.hashCode() : 0);
        result = 31 * result + (additionalPriorities != null ? additionalPriorities.hashCode() : 0);
        result = 31 * result + (localId != null ? localId.hashCode() : 0);
        result = 31 * result + (tableUI != null ? tableUI.hashCode() : 0);
        result = 31 * result + (removeBorder != null ? removeBorder.hashCode() : 0);
        result = 31 * result + (groupLabel != null ? groupLabel.hashCode() : 0);
        result = 31 * result + (xmlOrder != null ? xmlOrder.hashCode() : 0);
        result = 31 * result + (inplace != null ? inplace.hashCode() : 0);
        result = 31 * result + (positional != null ? positional.hashCode() : 0);
        result = 31 * result + (editorsIds != null ? editorsIds.hashCode() : 0);
        result = 31 * result + (allowedSubTypes != null ? allowedSubTypes.hashCode() : 0);
        result = 31 * result + (allowSubTypes != null ? allowSubTypes.hashCode() : 0);
        return result;
    }
}
