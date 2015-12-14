/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model;

import org.mule.module.extension.studio.model.element.BaseChildEditorElement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Nested extends EditorElement
{

    private String additionalNamespaces;
    private Integer windowHeight;
    private Boolean allowAny;
    private String childPrefix;
    private String childURI;
    private Boolean required;// TODO Debe ser un editor element?
    private String valuePersistence;
    private Boolean specialValuePersistenceRequired;
    private List<BaseChildEditorElement> childElements;

    @XmlAttribute
    public String getAdditionalNamespaces()
    {
        return additionalNamespaces;
    }

    public void setAdditionalNamespaces(String additionalNamespaces)
    {
        this.additionalNamespaces = additionalNamespaces;
    }

    @XmlAttribute
    public Integer getWindowHeight()
    {
        return windowHeight;
    }

    public void setWindowHeight(Integer windowHeight)
    {
        this.windowHeight = windowHeight;
    }

    @XmlAttribute
    public Boolean getAllowAny()
    {
        return allowAny;
    }

    public void setAllowAny(Boolean allowAny)
    {
        this.allowAny = allowAny;
    }

    @XmlAttribute
    public String getChildPrefix()
    {
        return childPrefix;
    }

    public void setChildPrefix(String childPrefix)
    {
        this.childPrefix = childPrefix;
    }

    @XmlAttribute
    public String getChildURI()
    {
        return childURI;
    }

    public void setChildURI(String childURI)
    {
        this.childURI = childURI;
    }

    @XmlElementRef
    public List<BaseChildEditorElement> getChildElements()
    {
        if (childElements == null)
        {
            childElements = new ArrayList<BaseChildEditorElement>();
        }
        return childElements;
    }

    public void setChildElements(List<BaseChildEditorElement> childElements)
    {
        this.childElements = childElements;
    }

    @Override
    public String toString()
    {
        return "Nested [additionalNamespaces=" + additionalNamespaces + ", childPrefix=" + childPrefix + ", childURI=" + childURI + ", getLocalId()=" + getLocalId()
               + ", getDescription()=" + getDescription() + ", getCaption()=" + getCaption() + "]";
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
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
    public Boolean getSpecialValuePersistenceRequired()
    {
        return specialValuePersistenceRequired;
    }

    public void setSpecialValuePersistenceRequired(Boolean specialValuePersistenceRequired)
    {
        this.specialValuePersistenceRequired = specialValuePersistenceRequired;
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

        Nested nested = (Nested) o;

        if (additionalNamespaces != null ? !additionalNamespaces.equals(nested.additionalNamespaces) : nested.additionalNamespaces != null)
        {
            return false;
        }
        if (windowHeight != null ? !windowHeight.equals(nested.windowHeight) : nested.windowHeight != null)
        {
            return false;
        }
        if (allowAny != null ? !allowAny.equals(nested.allowAny) : nested.allowAny != null)
        {
            return false;
        }
        if (childPrefix != null ? !childPrefix.equals(nested.childPrefix) : nested.childPrefix != null)
        {
            return false;
        }
        if (childURI != null ? !childURI.equals(nested.childURI) : nested.childURI != null)
        {
            return false;
        }
        if (required != null ? !required.equals(nested.required) : nested.required != null)
        {
            return false;
        }
        if (valuePersistence != null ? !valuePersistence.equals(nested.valuePersistence) : nested.valuePersistence != null)
        {
            return false;
        }
        if (specialValuePersistenceRequired != null ? !specialValuePersistenceRequired.equals(nested.specialValuePersistenceRequired) : nested.specialValuePersistenceRequired != null)
        {
            return false;
        }
        return !(childElements != null ? !childElements.equals(nested.childElements) : nested.childElements != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (additionalNamespaces != null ? additionalNamespaces.hashCode() : 0);
        result = 31 * result + (windowHeight != null ? windowHeight.hashCode() : 0);
        result = 31 * result + (allowAny != null ? allowAny.hashCode() : 0);
        result = 31 * result + (childPrefix != null ? childPrefix.hashCode() : 0);
        result = 31 * result + (childURI != null ? childURI.hashCode() : 0);
        result = 31 * result + (required != null ? required.hashCode() : 0);
        result = 31 * result + (valuePersistence != null ? valuePersistence.hashCode() : 0);
        result = 31 * result + (specialValuePersistenceRequired != null ? specialValuePersistenceRequired.hashCode() : 0);
        result = 31 * result + (childElements != null ? childElements.hashCode() : 0);
        return result;
    }
}
