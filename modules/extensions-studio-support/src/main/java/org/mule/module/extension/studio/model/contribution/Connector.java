/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.contribution;

import org.mule.module.extension.studio.model.ConnectivityTesting;
import org.mule.module.extension.studio.model.IEditorElementVisitor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Connector extends AbstractContributionEditorElement
{

    private ConnectivityTesting connectivityTesting;

    @XmlAttribute
    public ConnectivityTesting getConnectivityTesting()
    {
        return connectivityTesting;
    }

    public void setConnectivityTesting(ConnectivityTesting connectivityTesting)
    {
        this.connectivityTesting = connectivityTesting;
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
        result = prime * result + ((connectivityTesting == null) ? 0 : connectivityTesting.hashCode());
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
        Connector other = (Connector) obj;
        if (connectivityTesting != other.connectivityTesting)
        {
            return false;
        }
        return true;
    }
}
