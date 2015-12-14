/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.module.extension.studio.model.IEditorElementVisitor;

@XmlRootElement
public class SoapInterceptor extends BaseFieldEditorElement
{

    private Boolean allowMultiple;
    private Boolean inplace;

    @XmlAttribute
    public Boolean getAllowMultiple()
    {
        return allowMultiple;
    }

    public void setAllowMultiple(Boolean allowMultiple)
    {
        this.allowMultiple = allowMultiple;
    }

    @XmlAttribute
    public Boolean getInplace()
    {
        return inplace;
    }

    public void setInplace(Boolean inplace)
    {
        this.inplace = inplace;
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((allowMultiple == null) ? 0 : allowMultiple.hashCode());
        result = prime * result + ((inplace == null) ? 0 : inplace.hashCode());
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
        SoapInterceptor other = (SoapInterceptor) obj;
        if (allowMultiple == null)
        {
            if (other.allowMultiple != null)
            {
                return false;
            }
        }
        else if (!allowMultiple.equals(other.allowMultiple))
        {
            return false;
        }
        if (inplace == null)
        {
            if (other.inplace != null)
            {
                return false;
            }
        }
        else if (!inplace.equals(other.inplace))
        {
            return false;
        }
        return true;
    }
}
