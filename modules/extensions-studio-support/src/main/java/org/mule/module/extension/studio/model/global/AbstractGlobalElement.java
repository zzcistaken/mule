/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.global;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.mule.module.extension.studio.model.EditorElement;

@XmlSeeAlso({ Global.class, GlobalCloudConnector.class, CloudConnectorMessageSource.class, GlobalEndpoint.class, GlobalFilter.class, GlobalTransformer.class })
public abstract class AbstractGlobalElement extends EditorElement {

    private String category;// TODO Potential Enum;
    private String paletteCategory;// TODO Potential Enum;

    @XmlAttribute
    public String getPaletteCategory() {
        return paletteCategory;
    }

    public void setPaletteCategory(String paletteCategory) {
        this.paletteCategory = paletteCategory;
    }

    @XmlAttribute
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((paletteCategory == null) ? 0 : paletteCategory.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractGlobalElement other = (AbstractGlobalElement) obj;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (paletteCategory == null) {
            if (other.paletteCategory != null)
                return false;
        } else if (!paletteCategory.equals(other.paletteCategory))
            return false;
        return true;
    }
}
