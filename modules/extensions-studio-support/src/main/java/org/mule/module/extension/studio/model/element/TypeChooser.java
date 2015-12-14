/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element;

import org.mule.module.extension.studio.model.IEditorElementVisitor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "type-chooser")
public class TypeChooser extends BaseFieldEditorElement
{

    private String associatedConfig;
    private MetaDataKeyParamAffectsType affects;
    private Boolean disableButton;

    @XmlAttribute
    public MetaDataKeyParamAffectsType getAffects()
    {
        return affects;
    }

    public void setAffects(MetaDataKeyParamAffectsType affects)
    {
        this.affects = affects;
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getAssociatedConfig()
    {
        return associatedConfig;
    }

    public void setAssociatedConfig(String associatedConfig)
    {
        this.associatedConfig = associatedConfig;
    }

    @XmlAttribute
    public Boolean getDisableButton()
    {
        return disableButton;
    }

    public void setDisableButton(Boolean disableButton)
    {
        this.disableButton = disableButton;
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

        TypeChooser that = (TypeChooser) o;

        if (associatedConfig != null ? !associatedConfig.equals(that.associatedConfig) : that.associatedConfig != null)
        {
            return false;
        }
        if (affects != that.affects)
        {
            return false;
        }
        return !(disableButton != null ? !disableButton.equals(that.disableButton) : that.disableButton != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (associatedConfig != null ? associatedConfig.hashCode() : 0);
        result = 31 * result + (affects != null ? affects.hashCode() : 0);
        result = 31 * result + (disableButton != null ? disableButton.hashCode() : 0);
        return result;
    }
}
