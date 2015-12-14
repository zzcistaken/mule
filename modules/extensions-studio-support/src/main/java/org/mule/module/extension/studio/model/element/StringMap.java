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

@XmlRootElement(name = "string-map")
public class StringMap extends AbstractElementController
{

    // TODO: EEE Review this names and put something more useful. This probably are the root field reference name, and the child field reference name
    private String ref;
    private String ref1;
    private String listName;

    @XmlAttribute(name = "ref")
    public String getRef()
    {
        return ref;
    }

    public void setRef(String ref)
    {
        this.ref = ref;
    }

    @XmlAttribute(name = "ref1")
    public String getRef1()
    {
        return ref1;
    }

    public void setRef1(String ref1)
    {
        this.ref1 = ref1;
    }

    @XmlAttribute
    public String getListName()
    {
        return listName;
    }

    public void setListName(String listName)
    {
        this.listName = listName;
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
        result = prime * result + ((listName == null) ? 0 : listName.hashCode());
        result = prime * result + ((ref == null) ? 0 : ref.hashCode());
        result = prime * result + ((ref1 == null) ? 0 : ref1.hashCode());
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
        StringMap other = (StringMap) obj;
        if (listName == null)
        {
            if (other.listName != null)
            {
                return false;
            }
        }
        else if (!listName.equals(other.listName))
        {
            return false;
        }
        if (ref == null)
        {
            if (other.ref != null)
            {
                return false;
            }
        }
        else if (!ref.equals(other.ref))
        {
            return false;
        }
        if (ref1 == null)
        {
            if (other.ref1 != null)
            {
                return false;
            }
        }
        else if (!ref1.equals(other.ref1))
        {
            return false;
        }
        return true;
    }
}
