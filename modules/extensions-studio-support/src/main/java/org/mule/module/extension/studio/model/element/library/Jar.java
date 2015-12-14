/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element.library;

import org.mule.module.extension.studio.model.IEditorElementVisitor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Jar extends AbstractBaseLibrary
{

    private String className;
    private String fileName;

    @XmlAttribute
    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    @XmlAttribute
    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "Jar [className=" + className + ", fileName=" + fileName + ", getName()=" + getName() + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
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
        Jar other = (Jar) obj;
        if (className == null)
        {
            if (other.className != null)
            {
                return false;
            }
        }
        else if (!className.equals(other.className))
        {
            return false;
        }
        if (fileName == null)
        {
            if (other.fileName != null)
            {
                return false;
            }
        }
        else if (!fileName.equals(other.fileName))
        {
            return false;
        }
        return true;
    }

}
