/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cloud-connector")
public class CloudConnector extends AbstractPaletteComponent
{

    private String metaDataStaticKey;
    private String returnType;
    private Boolean supportsUserDefinedMetaData;
    private String forceMetadataRefreshAtrributes;// TODO Fiel id a CSV
    private String categories;

    @XmlAttribute
    public String getReturnType()
    {
        return returnType;
    }

    public void setReturnType(String returnType)
    {
        this.returnType = returnType;
    }

    @XmlAttribute
    public String getMetaDataStaticKey()
    {
        return metaDataStaticKey;
    }

    public void setMetaDataStaticKey(String metaDataStaticKey)
    {
        this.metaDataStaticKey = metaDataStaticKey;
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getSupportsUserDefinedMetaData()
    {
        return supportsUserDefinedMetaData;
    }

    public void setSupportsUserDefinedMetaData(Boolean supportsUserDefinedMetaData)
    {
        this.supportsUserDefinedMetaData = supportsUserDefinedMetaData;
    }

    @XmlAttribute
    public String getForceMetadataRefreshAtrributes()
    {
        return forceMetadataRefreshAtrributes;
    }

    public void setForceMetadataRefreshAtrributes(String forceMetadataRefreshAtrributes)
    {
        this.forceMetadataRefreshAtrributes = forceMetadataRefreshAtrributes;
    }

    @XmlAttribute
    public String getCategories()
    {
        return categories;
    }

    public void setCategories(String categories)
    {
        this.categories = categories;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((categories == null) ? 0 : categories.hashCode());
        result = prime * result + ((forceMetadataRefreshAtrributes == null) ? 0 : forceMetadataRefreshAtrributes.hashCode());
        result = prime * result + ((metaDataStaticKey == null) ? 0 : metaDataStaticKey.hashCode());
        result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
        result = prime * result + ((supportsUserDefinedMetaData == null) ? 0 : supportsUserDefinedMetaData.hashCode());
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
        CloudConnector other = (CloudConnector) obj;
        if (categories == null)
        {
            if (other.categories != null)
            {
                return false;
            }
        }
        else if (!categories.equals(other.categories))
        {
            return false;
        }
        if (forceMetadataRefreshAtrributes == null)
        {
            if (other.forceMetadataRefreshAtrributes != null)
            {
                return false;
            }
        }
        else if (!forceMetadataRefreshAtrributes.equals(other.forceMetadataRefreshAtrributes))
        {
            return false;
        }
        if (metaDataStaticKey == null)
        {
            if (other.metaDataStaticKey != null)
            {
                return false;
            }
        }
        else if (!metaDataStaticKey.equals(other.metaDataStaticKey))
        {
            return false;
        }
        if (returnType == null)
        {
            if (other.returnType != null)
            {
                return false;
            }
        }
        else if (!returnType.equals(other.returnType))
        {
            return false;
        }
        if (supportsUserDefinedMetaData == null)
        {
            if (other.supportsUserDefinedMetaData != null)
            {
                return false;
            }
        }
        else if (!supportsUserDefinedMetaData.equals(other.supportsUserDefinedMetaData))
        {
            return false;
        }
        return true;
    }
}
