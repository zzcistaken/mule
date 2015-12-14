/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "keywords")
public class KeywordSet extends AbstractEditorElement
{

    private List<Keyword> keywords;

    @XmlElement(name = "keyword")
    public List<Keyword> getKeywords()
    {
        if (keywords == null)
        {
            keywords = new ArrayList<Keyword>();
        }
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords)
    {
        this.keywords = keywords;
    }

    @Override
    public String toString()
    {
        return "KeywordSet";
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
        result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
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
        KeywordSet other = (KeywordSet) obj;
        if (keywords == null)
        {
            if (other.keywords != null)
            {
                return false;
            }
        }
        else if (!keywords.equals(other.keywords))
        {
            return false;
        }
        return true;
    }

}
