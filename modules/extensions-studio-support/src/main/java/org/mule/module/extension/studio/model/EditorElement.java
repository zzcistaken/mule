/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model;

import org.mule.module.extension.studio.model.element.AttributeCategory;
import org.mule.module.extension.studio.model.element.FixedAttribute;
import org.mule.module.extension.studio.model.global.AbstractGlobalElement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({AbstractContainer.class, AbstractGlobalElement.class, AbstractPaletteComponent.class, Component.class, Nested.class})
public abstract class EditorElement extends AbstractEditorElement
{

    private String caption;
    private String description;
    private String localId;
    private String xmlname;
    private String icon;
    private String image;
    private Boolean isAbstract;
    //@ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.meta.IComponentValidator.class)
    private String componentValidator;
    private List<FixedAttribute> fixedAttributes;
    private List<AttributeCategory> attributeCategories;
    private String doNotInherit;
    private String defaultDocName;// Solo lo usan Container y Component
    private Boolean showProposalInXML;// Solo se usa en endpoint,connector,global-endpoint
    private String extendsElement;
    private Boolean supportsInbound;
    private String aliasId;
    private Boolean hiddenFromXML;

    @XmlAttribute(required = true)
    public String getLocalId()
    {
        return localId;
    }

    public void setLocalId(String localId)
    {
        this.localId = localId;
    }

    @XmlAttribute
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @XmlAttribute
    public String getCaption()
    {
        return caption;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    @XmlAttribute
    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    @XmlAttribute
    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    @XmlElement(name = "fixedAttribute")
    public List<FixedAttribute> getFixedAttributes()
    {
        if(fixedAttributes==null)
        {
            fixedAttributes = new ArrayList<FixedAttribute>();
        }
        return fixedAttributes;
    }

    public void setFixedAttributes(List<FixedAttribute> fixedAttributes)
    {
        this.fixedAttributes = fixedAttributes;
    }

    @XmlElement(name = "attribute-category")
    public List<AttributeCategory> getAttributeCategories()
    {
        if (attributeCategories == null)
        {
            attributeCategories = new ArrayList<AttributeCategory>();
        }
        return attributeCategories;
    }

    public void setAttributeCategories(List<AttributeCategory> attributeCategories)
    {
        this.attributeCategories = attributeCategories;
    }

    @XmlAttribute
    public String getXmlname()
    {
        return xmlname;
    }

    public void setXmlname(String xmlname)
    {
        this.xmlname = xmlname;
    }

    @Override
    public String toString()
    {
        return " [caption=" + caption + ", localId=" + localId + "]";
    }

    @XmlAttribute(name = "abstract")
    public Boolean getIsAbstract()
    {
        return isAbstract;
    }

    public void setIsAbstract(Boolean isAbstract)
    {
        this.isAbstract = isAbstract;
    }

    @XmlAttribute
    public String getComponentValidator()
    {
        return componentValidator;
    }

    public void setComponentValidator(String componentValidator)
    {
        this.componentValidator = componentValidator;
    }

    @XmlAttribute
    public String getDoNotInherit()
    {
        return doNotInherit;
    }

    public void setDoNotInherit(String doNotInherit)
    {
        this.doNotInherit = doNotInherit;
    }

    @XmlAttribute
    public String getDefaultDocName()
    {
        return defaultDocName;
    }

    public void setDefaultDocName(String defaultDocName)
    {
        this.defaultDocName = defaultDocName;
    }

    @XmlAttribute
    public Boolean getShowProposalInXML()
    {
        return showProposalInXML;
    }

    public void setShowProposalInXML(Boolean showProposalInXML)
    {
        this.showProposalInXML = showProposalInXML;
    }

    @XmlAttribute(name = "extends")
    public String getExtendsElement()
    {
        return extendsElement;
    }

    public void setExtendsElement(String extendsElement)
    {
        this.extendsElement = extendsElement;
    }

    @XmlAttribute
    public Boolean getSupportsInbound()
    {
        return supportsInbound;
    }

    public void setSupportsInbound(Boolean supportsInbound)
    {
        this.supportsInbound = supportsInbound;
    }

    @XmlAttribute
    public String getAliasId()
    {
        return aliasId;
    }

    public void setAliasId(String aliasId)
    {
        this.aliasId = aliasId;
    }

    @XmlAttribute
    public Boolean getHiddenFromXML()
    {
        return hiddenFromXML;
    }

    public void setHiddenFromXML(Boolean hiddenFromXML)
    {
        this.hiddenFromXML = hiddenFromXML;
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

        EditorElement that = (EditorElement) o;

        if (caption != null ? !caption.equals(that.caption) : that.caption != null)
        {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null)
        {
            return false;
        }
        if (localId != null ? !localId.equals(that.localId) : that.localId != null)
        {
            return false;
        }
        if (xmlname != null ? !xmlname.equals(that.xmlname) : that.xmlname != null)
        {
            return false;
        }
        if (icon != null ? !icon.equals(that.icon) : that.icon != null)
        {
            return false;
        }
        if (image != null ? !image.equals(that.image) : that.image != null)
        {
            return false;
        }
        if (isAbstract != null ? !isAbstract.equals(that.isAbstract) : that.isAbstract != null)
        {
            return false;
        }
        if (componentValidator != null ? !componentValidator.equals(that.componentValidator) : that.componentValidator != null)
        {
            return false;
        }
        if (fixedAttributes != null ? !fixedAttributes.equals(that.fixedAttributes) : that.fixedAttributes != null)
        {
            return false;
        }
        if (attributeCategories != null ? !attributeCategories.equals(that.attributeCategories) : that.attributeCategories != null)
        {
            return false;
        }
        if (doNotInherit != null ? !doNotInherit.equals(that.doNotInherit) : that.doNotInherit != null)
        {
            return false;
        }
        if (defaultDocName != null ? !defaultDocName.equals(that.defaultDocName) : that.defaultDocName != null)
        {
            return false;
        }
        if (showProposalInXML != null ? !showProposalInXML.equals(that.showProposalInXML) : that.showProposalInXML != null)
        {
            return false;
        }
        if (extendsElement != null ? !extendsElement.equals(that.extendsElement) : that.extendsElement != null)
        {
            return false;
        }
        if (supportsInbound != null ? !supportsInbound.equals(that.supportsInbound) : that.supportsInbound != null)
        {
            return false;
        }
        if (aliasId != null ? !aliasId.equals(that.aliasId) : that.aliasId != null)
        {
            return false;
        }
        return !(hiddenFromXML != null ? !hiddenFromXML.equals(that.hiddenFromXML) : that.hiddenFromXML != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (localId != null ? localId.hashCode() : 0);
        result = 31 * result + (xmlname != null ? xmlname.hashCode() : 0);
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (isAbstract != null ? isAbstract.hashCode() : 0);
        result = 31 * result + (componentValidator != null ? componentValidator.hashCode() : 0);
        result = 31 * result + (fixedAttributes != null ? fixedAttributes.hashCode() : 0);
        result = 31 * result + (attributeCategories != null ? attributeCategories.hashCode() : 0);
        result = 31 * result + (doNotInherit != null ? doNotInherit.hashCode() : 0);
        result = 31 * result + (defaultDocName != null ? defaultDocName.hashCode() : 0);
        result = 31 * result + (showProposalInXML != null ? showProposalInXML.hashCode() : 0);
        result = 31 * result + (extendsElement != null ? extendsElement.hashCode() : 0);
        result = 31 * result + (supportsInbound != null ? supportsInbound.hashCode() : 0);
        result = 31 * result + (aliasId != null ? aliasId.hashCode() : 0);
        result = 31 * result + (hiddenFromXML != null ? hiddenFromXML.hashCode() : 0);
        return result;
    }
}
