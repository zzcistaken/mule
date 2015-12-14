/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element.library;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({Jar.class, NativeLibrary.class})
@XmlRootElement
public abstract class AbstractBaseLibrary extends AbstractLibrary
{
    private String emptyLocationLabel;
    private boolean externalPluginDependency;
    private String name;
    private String targetRuntimeFolder;

    @XmlAttribute
    public String getTargetRuntimeFolder()
    {
        return targetRuntimeFolder;
    }

    public void setTargetRuntimeFolder(String targetRuntimeFolder)
    {
        this.targetRuntimeFolder = targetRuntimeFolder;
    }

    @XmlAttribute
    public String getEmptyLocationLabel()
    {
        return emptyLocationLabel;
    }

    public void setEmptyLocationLabel(String emptyLocationLabel)
    {
        this.emptyLocationLabel = emptyLocationLabel;
    }

    @XmlAttribute
    public boolean isExternalPluginDependency()
    {
        return externalPluginDependency;
    }

    public void setExternalPluginDependency(boolean externalPluginDependency)
    {
        this.externalPluginDependency = externalPluginDependency;
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

        AbstractBaseLibrary that = (AbstractBaseLibrary) o;

        if (externalPluginDependency != that.externalPluginDependency)
        {
            return false;
        }
        if (emptyLocationLabel != null ? !emptyLocationLabel.equals(that.emptyLocationLabel) : that.emptyLocationLabel != null)
        {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null)
        {
            return false;
        }

        return !(targetRuntimeFolder != null ? !targetRuntimeFolder.equals(that.targetRuntimeFolder) : that.targetRuntimeFolder != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (emptyLocationLabel != null ? emptyLocationLabel.hashCode() : 0);
        result = 31 * result + (externalPluginDependency ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (targetRuntimeFolder != null ? targetRuntimeFolder.hashCode() : 0);
        return result;
    }
}