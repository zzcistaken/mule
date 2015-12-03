/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.module.extension.studio.model.AbstractEditorElement;
import org.mule.module.extension.studio.model.IEditorElementVisitor;

@XmlRootElement
public class Case extends AbstractEditorElement {

    private String id;
    private List<BaseChildEditorElement> childElements;

    @XmlElementRef
    public List<BaseChildEditorElement> getChildElements() {
        if (childElements == null) {
            childElements = new ArrayList<BaseChildEditorElement>();
        }
        return childElements;
    }

    public void setChildElements(List<BaseChildEditorElement> childElements) {
        this.childElements = childElements;
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void accept(IEditorElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((childElements == null) ? 0 : childElements.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        Case other = (Case) obj;
        if (childElements == null) {
            if (other.childElements != null)
                return false;
        } else if (!childElements.equals(other.childElements))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
