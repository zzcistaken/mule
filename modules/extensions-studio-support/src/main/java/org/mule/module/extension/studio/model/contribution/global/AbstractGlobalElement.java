/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.contribution.global;

import org.mule.module.extension.studio.model.AbstractBaseEditorElement;
import org.mule.module.extension.studio.model.element.library.RequiredLibraries;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({Global.class, GlobalCloudConnector.class, GlobalEndpoint.class, GlobalFilter.class, GlobalTransformer.class})
public abstract class AbstractGlobalElement extends AbstractBaseEditorElement
{

    private String category;
    private String paletteCategory;
    private RequiredLibraries required;

    @XmlElement
    public RequiredLibraries getRequired()
    {
        return required;
    }

    public void setRequired(RequiredLibraries required)
    {
        this.required = required;
    }

    @XmlAttribute
    public String getPaletteCategory()
    {
        return paletteCategory;
    }

    public void setPaletteCategory(String paletteCategory)
    {
        this.paletteCategory = paletteCategory;
    }

    @XmlAttribute
    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
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

        AbstractGlobalElement that = (AbstractGlobalElement) o;

        if (category != null ? !category.equals(that.category) : that.category != null)
        {
            return false;
        }
        if (paletteCategory != null ? !paletteCategory.equals(that.paletteCategory) : that.paletteCategory != null)
        {
            return false;
        }
        return !(required != null ? !required.equals(that.required) : that.required != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (paletteCategory != null ? paletteCategory.hashCode() : 0);
        result = 31 * result + (required != null ? required.hashCode() : 0);
        return result;
    }
}
