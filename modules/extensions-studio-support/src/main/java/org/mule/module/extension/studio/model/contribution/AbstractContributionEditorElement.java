/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.contribution;

import org.mule.module.extension.studio.model.AbstractBaseEditorElement;
import org.mule.module.extension.studio.model.KeywordSet;
import org.mule.module.extension.studio.model.MetaDataBehaviour;
import org.mule.module.extension.studio.model.RequiredSetAlternatives;
import org.mule.module.extension.studio.model.element.library.RequiredLibraries;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({CloudConnector.class, Component.class, Connector.class, Container.class, Endpoint.class, Flow.class, Filter.class, MultiSource.class, Pattern.class, Transformer.class, Wizard.class})
public abstract class AbstractContributionEditorElement extends AbstractBaseEditorElement
{

    private String completionProposalDocName;
    private MetaDataBehaviour metaData;
    private KeywordSet keywords;
    private RequiredSetAlternatives requiredSetAlternatives;
    private String category;
    private String paletteCategory;
    private RequiredLibraries required;

    @XmlElement
    public RequiredLibraries getRequired()
    {
        return required;
    }

    public void setRequired(RequiredLibraries required)
    {
        this.required = required;
    }

    @XmlAttribute
    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    @XmlElement(name = "required-set-alternatives")
    public RequiredSetAlternatives getRequiredSetAlternatives()
    {
        return requiredSetAlternatives;
    }

    public void setRequiredSetAlternatives(RequiredSetAlternatives requiredSetAlternatives)
    {
        this.requiredSetAlternatives = requiredSetAlternatives;
    }

    @XmlElement
    public KeywordSet getKeywords()
    {
        return keywords;
    }

    public void setKeywords(KeywordSet keywords)
    {
        this.keywords = keywords;
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

    @XmlAttribute
    public String getCompletionProposalDocName()
    {
        return completionProposalDocName;
    }

    public void setCompletionProposalDocName(String completionProposalDocName)
    {
        this.completionProposalDocName = completionProposalDocName;
    }

    @XmlAttribute
    public String getPaletteCategory()
    {
        return paletteCategory;
    }

    public void setPaletteCategory(String paletteCategory)
    {
        this.paletteCategory = paletteCategory;
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

        AbstractContributionEditorElement that = (AbstractContributionEditorElement) o;

        if (completionProposalDocName != null ? !completionProposalDocName.equals(that.completionProposalDocName) : that.completionProposalDocName != null)
        {
            return false;
        }
        if (metaData != that.metaData)
        {
            return false;
        }
        if (keywords != null ? !keywords.equals(that.keywords) : that.keywords != null)
        {
            return false;
        }
        if (requiredSetAlternatives != null ? !requiredSetAlternatives.equals(that.requiredSetAlternatives) : that.requiredSetAlternatives != null)
        {
            return false;
        }
        if (category != null ? !category.equals(that.category) : that.category != null)
        {
            return false;
        }
        if (paletteCategory != null ? !paletteCategory.equals(that.paletteCategory) : that.paletteCategory != null)
        {
            return false;
        }
        return !(required != null ? !required.equals(that.required) : that.required != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (completionProposalDocName != null ? completionProposalDocName.hashCode() : 0);
        result = 31 * result + (metaData != null ? metaData.hashCode() : 0);
        result = 31 * result + (keywords != null ? keywords.hashCode() : 0);
        result = 31 * result + (requiredSetAlternatives != null ? requiredSetAlternatives.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (paletteCategory != null ? paletteCategory.hashCode() : 0);
        result = 31 * result + (required != null ? required.hashCode() : 0);
        return result;
    }
}
