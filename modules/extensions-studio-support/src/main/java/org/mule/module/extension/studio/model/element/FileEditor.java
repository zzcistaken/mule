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

@XmlRootElement(name = "file")
public class FileEditor extends BaseStringEditor
{

    private Boolean relativeToProject;

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "FileEditor [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @XmlAttribute
    public Boolean getRelativeToProject()
    {
        return relativeToProject;
    }

    public void setRelativeToProject(Boolean relativeToProject)
    {
        this.relativeToProject = relativeToProject;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((relativeToProject == null) ? 0 : relativeToProject.hashCode());
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
        FileEditor other = (FileEditor) obj;
        if (relativeToProject == null)
        {
            if (other.relativeToProject != null)
            {
                return false;
            }
        }
        else if (!relativeToProject.equals(other.relativeToProject))
        {
            return false;
        }
        return true;
    }

}
