/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element.library;

import org.mule.module.extension.studio.model.AbstractEditorElement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({AbstractBaseLibrary.class, LibrarySet.class})
public abstract class AbstractLibrary extends AbstractEditorElement
{

    private String targetFolder;
    private String name;

    @XmlAttribute
    public String getTargetFolder()
    {
        return targetFolder;
    }

    public void setTargetFolder(String targetFolder)
    {
        this.targetFolder = targetFolder;
    }

    @XmlAttribute
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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

        AbstractLibrary that = (AbstractLibrary) o;

        if (targetFolder != null ? !targetFolder.equals(that.targetFolder) : that.targetFolder != null)
        {
            return false;
        }
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (targetFolder != null ? targetFolder.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
