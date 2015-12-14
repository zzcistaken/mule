/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.global;

import org.mule.module.extension.studio.model.ConnectivityTesting;
import org.mule.module.extension.studio.model.IEditorElementVisitor;
import org.mule.module.extension.studio.model.MetaDataBehaviour;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "global-cloud-connector")
public class GlobalCloudConnector extends AbstractGlobalElement
{

    private Boolean supportsUserDefinedMetaData;
    private Boolean usesMetadataTypes;
    private String connectivityTestingLabel;
    private String metaDataKeyDisplay;
    private ConnectivityTesting connectivityTesting;
    private MetaDataBehaviour metaData;

    @XmlAttribute
    public ConnectivityTesting getConnectivityTesting()
    {
        return connectivityTesting;
    }

    public void setConnectivityTesting(ConnectivityTesting connectivityTesting)
    {
        this.connectivityTesting = connectivityTesting;
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
    public Boolean getUsesMetadataTypes()
    {
        return usesMetadataTypes;
    }

    public void setUsesMetadataTypes(Boolean usesMetadataTypes)
    {
        this.usesMetadataTypes = usesMetadataTypes;
    }

    @XmlAttribute
    public String getConnectivityTestingLabel()
    {
        return connectivityTestingLabel;
    }

    public void setConnectivityTestingLabel(String connectivityTestingLabel)
    {
        this.connectivityTestingLabel = connectivityTestingLabel;
    }

    @XmlAttribute
    public String getMetaDataKeyDisplay()
    {
        return metaDataKeyDisplay;
    }

    public void setMetaDataKeyDisplay(String metaDataKeyDisplay)
    {
        this.metaDataKeyDisplay = metaDataKeyDisplay;
    }

    @Override
    public String toString()
    {
        return "GlobalCloudConnector [supportsUserDefinedMetaData=" + supportsUserDefinedMetaData + ", usesMetadataTypes=" + usesMetadataTypes + ", connectivityTestingLabel="
               + connectivityTestingLabel + ", metaDataKeyDisplay=" + metaDataKeyDisplay + ", getLocalId()=" + getLocalId() + ", getCaption()=" + getCaption() + "]";
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public MetaDataBehaviour getMetaData()
    {
        return metaData;
    }

    public void setMetaData(MetaDataBehaviour metaData)
    {
        this.metaData = metaData;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((connectivityTesting == null) ? 0 : connectivityTesting.hashCode());
        result = prime * result + ((connectivityTestingLabel == null) ? 0 : connectivityTestingLabel.hashCode());
        result = prime * result + ((metaData == null) ? 0 : metaData.hashCode());
        result = prime * result + ((metaDataKeyDisplay == null) ? 0 : metaDataKeyDisplay.hashCode());
        result = prime * result + ((supportsUserDefinedMetaData == null) ? 0 : supportsUserDefinedMetaData.hashCode());
        result = prime * result + ((usesMetadataTypes == null) ? 0 : usesMetadataTypes.hashCode());
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
        GlobalCloudConnector other = (GlobalCloudConnector) obj;
        if (connectivityTesting != other.connectivityTesting)
        {
            return false;
        }
        if (connectivityTestingLabel == null)
        {
            if (other.connectivityTestingLabel != null)
            {
                return false;
            }
        }
        else if (!connectivityTestingLabel.equals(other.connectivityTestingLabel))
        {
            return false;
        }
        if (metaData != other.metaData)
        {
            return false;
        }
        if (metaDataKeyDisplay == null)
        {
            if (other.metaDataKeyDisplay != null)
            {
                return false;
            }
        }
        else if (!metaDataKeyDisplay.equals(other.metaDataKeyDisplay))
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
        if (usesMetadataTypes == null)
        {
            if (other.usesMetadataTypes != null)
            {
                return false;
            }
        }
        else if (!usesMetadataTypes.equals(other.usesMetadataTypes))
        {
            return false;
        }
        return true;
    }
}
