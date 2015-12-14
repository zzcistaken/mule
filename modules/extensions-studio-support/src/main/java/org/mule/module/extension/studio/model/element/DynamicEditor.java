/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element;

import org.mule.module.extension.studio.model.IEditorElementVisitor;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dynamicEditors")
public class DynamicEditor extends BaseFieldEditorElement
{

    //@ClassPicker
    private String classAttribute;

    private List<EditorRef> editorReferences;

    @XmlElementRef
    public List<EditorRef> getEditorReferences()
    {
        if (editorReferences == null)
        {
            editorReferences = new ArrayList<EditorRef>();
        }
        return editorReferences;
    }

    public void setEditorReferences(List<EditorRef> editorReferences)
    {
        this.editorReferences = editorReferences;
    }

    @Override
    public String toString()
    {
        return "DynamicEditor [getName()=" + getName() + ", getCaption()=" + getCaption() + ", getDescription()=" + getDescription() + "]";
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute(name = "class")
    public String getClassAttribute()
    {
        return classAttribute;
    }

    public void setClassAttribute(String classAttribute)
    {
        this.classAttribute = classAttribute;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((classAttribute == null) ? 0 : classAttribute.hashCode());
        result = prime * result + ((editorReferences == null) ? 0 : editorReferences.hashCode());
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
        DynamicEditor other = (DynamicEditor) obj;
        if (classAttribute == null)
        {
            if (other.classAttribute != null)
            {
                return false;
            }
        }
        else if (!classAttribute.equals(other.classAttribute))
        {
            return false;
        }
        if (editorReferences == null)
        {
            if (other.editorReferences != null)
            {
                return false;
            }
        }
        else if (!editorReferences.equals(other.editorReferences))
        {
            return false;
        }
        return true;
    }
}
