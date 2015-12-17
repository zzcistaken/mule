/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.contribution.global;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.module.extension.studio.model.IEditorElementVisitor;
import org.mule.module.extension.studio.model.MessageExchangePattern;

@XmlRootElement(name = "global-endpoint")
public class GlobalEndpoint extends AbstractGlobalElement
{

    private MessageExchangePattern defaultMep;
    private Boolean supportsOutbound;

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public MessageExchangePattern getDefaultMep()
    {
        return defaultMep;
    }

    public void setDefaultMep(MessageExchangePattern defaultMep)
    {
        this.defaultMep = defaultMep;
    }

    @XmlAttribute
    public Boolean getSupportsOutbound()
    {
        return supportsOutbound;
    }

    public void setSupportsOutbound(Boolean supportsOutbound)
    {
        this.supportsOutbound = supportsOutbound;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((defaultMep == null) ? 0 : defaultMep.hashCode());
        result = prime * result + ((supportsOutbound == null) ? 0 : supportsOutbound.hashCode());
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
        GlobalEndpoint other = (GlobalEndpoint) obj;
        if (defaultMep != other.defaultMep)
        {
            return false;
        }
        if (supportsOutbound == null)
        {
            if (other.supportsOutbound != null)
            {
                return false;
            }
        }
        else if (!supportsOutbound.equals(other.supportsOutbound))
        {
            return false;
        }
        return true;
    }
}
