/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Wizard extends AbstractPaletteComponent
{

    private String onFinish;

    @XmlAttribute
    public String getOnFinish()
    {
        return onFinish;
    }

    public void setOnFinish(String onFinish)
    {
        this.onFinish = onFinish;
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
        result = prime * result + ((onFinish == null) ? 0 : onFinish.hashCode());
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
        Wizard other = (Wizard) obj;
        if (onFinish == null)
        {
            if (other.onFinish != null)
            {
                return false;
            }
        }
        else if (!onFinish.equals(other.onFinish))
        {
            return false;
        }
        return true;
    }
}
