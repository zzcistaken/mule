/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element;

import org.mule.module.extension.studio.model.element.macro.ElementControllerList;
import org.mule.module.extension.studio.model.element.macro.ElementControllerListNoExpression;
import org.mule.module.extension.studio.model.element.macro.ElementControllerListOfMap;
import org.mule.module.extension.studio.model.element.macro.ElementControllerListOfPojo;
import org.mule.module.extension.studio.model.element.macro.ElementControllerMap;
import org.mule.module.extension.studio.model.element.macro.ElementControllerMapNoExpression;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ElementControllerList.class, ElementControllerListNoExpression.class, ElementControllerMap.class, ElementControllerMapNoExpression.class,
        ElementControllerListOfMap.class, ElementControllerListOfPojo.class, StringMap.class,})
public abstract class AbstractElementController extends BaseChildEditorElement
{

    // TODO: ver si puede ir en el padre.
    private Boolean required;

    private String localName;
    private String itemName;
    private String javaType;

    @XmlAttribute
    public String getJavaType()
    {
        return javaType;
    }

    public void setJavaType(String javaType)
    {
        this.javaType = javaType;
    }

    @XmlAttribute
    public Boolean getRequired()
    {
        return required;
    }

    public void setRequired(Boolean required)
    {
        this.required = required;
    }

    @XmlAttribute
    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    @XmlAttribute
    public String getLocalName()
    {
        return localName;
    }

    public void setLocalName(String localName)
    {
        this.localName = localName;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((itemName == null) ? 0 : itemName.hashCode());
        result = prime * result + ((javaType == null) ? 0 : javaType.hashCode());
        result = prime * result + ((localName == null) ? 0 : localName.hashCode());
        result = prime * result + ((required == null) ? 0 : required.hashCode());
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
        AbstractElementController other = (AbstractElementController) obj;
        if (itemName == null)
        {
            if (other.itemName != null)
            {
                return false;
            }
        }
        else if (!itemName.equals(other.itemName))
        {
            return false;
        }
        if (javaType == null)
        {
            if (other.javaType != null)
            {
                return false;
            }
        }
        else if (!javaType.equals(other.javaType))
        {
            return false;
        }
        if (localName == null)
        {
            if (other.localName != null)
            {
                return false;
            }
        }
        else if (!localName.equals(other.localName))
        {
            return false;
        }
        if (required == null)
        {
            if (other.required != null)
            {
                return false;
            }
        }
        else if (!required.equals(other.required))
        {
            return false;
        }
        return true;
    }
}
