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

@XmlRootElement(name = "text")
public class TextEditor extends BaseFieldEditorElement
{

    private Boolean wrapWithCDATA;
    private Integer textAreaHeight;
    private Integer textAreaWidth;
    private String language;
    private Boolean isToElement;
    //@ClassPicker(mustImplement = org.mule.tooling.ui.modules.core.widgets.editors.TextViewerCreator.class)
    private String customTextViewerCreator;
    private String nestedName;
    //It is never used, but might be helpful
    private String defaultValue;

    @Override
    public String toString()
    {
        return "TextEditor [getName()=" + getName() + ", getCaption()=" + getCaption() + ", getDescription()=" + getDescription() + "]";
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getWrapWithCDATA()
    {
        return wrapWithCDATA;
    }

    public void setWrapWithCDATA(Boolean wrapWithCDATA)
    {
        this.wrapWithCDATA = wrapWithCDATA;
    }

    @XmlAttribute(name = "textAreaHeight")
    public Integer getTextAreaHeight()
    {
        return textAreaHeight;
    }

    public void setTextAreaHeight(Integer textAreaHeight)
    {
        this.textAreaHeight = textAreaHeight;
    }

    @XmlAttribute(name = "textAreaWidth")
    public Integer getTextAreaWidth()
    {
        return textAreaWidth;
    }

    public void setTextAreaWidth(Integer textAreaWidth)
    {
        this.textAreaWidth = textAreaWidth;
    }

    @XmlAttribute
    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    @XmlAttribute
    public Boolean getIsToElement()
    {
        return isToElement;
    }

    public void setIsToElement(Boolean isToElement)
    {
        this.isToElement = isToElement;
    }

    public String getCustomTextViewerCreator()
    {
        return customTextViewerCreator;
    }

    public void setCustomTextViewerCreator(String customTextViewerCreator)
    {
        this.customTextViewerCreator = customTextViewerCreator;
    }

    @XmlAttribute
    public String getNestedName()
    {
        return nestedName;
    }

    public void setNestedName(String nestedName)
    {
        this.nestedName = nestedName;
    }

    @XmlAttribute
    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
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

        TextEditor that = (TextEditor) o;

        if (wrapWithCDATA != null ? !wrapWithCDATA.equals(that.wrapWithCDATA) : that.wrapWithCDATA != null)
        {
            return false;
        }
        if (textAreaHeight != null ? !textAreaHeight.equals(that.textAreaHeight) : that.textAreaHeight != null)
        {
            return false;
        }
        if (textAreaWidth != null ? !textAreaWidth.equals(that.textAreaWidth) : that.textAreaWidth != null)
        {
            return false;
        }
        if (language != null ? !language.equals(that.language) : that.language != null)
        {
            return false;
        }
        if (isToElement != null ? !isToElement.equals(that.isToElement) : that.isToElement != null)
        {
            return false;
        }
        if (customTextViewerCreator != null ? !customTextViewerCreator.equals(that.customTextViewerCreator) : that.customTextViewerCreator != null)
        {
            return false;
        }
        if (nestedName != null ? !nestedName.equals(that.nestedName) : that.nestedName != null)
        {
            return false;
        }
        return !(defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (wrapWithCDATA != null ? wrapWithCDATA.hashCode() : 0);
        result = 31 * result + (textAreaHeight != null ? textAreaHeight.hashCode() : 0);
        result = 31 * result + (textAreaWidth != null ? textAreaWidth.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (isToElement != null ? isToElement.hashCode() : 0);
        result = 31 * result + (customTextViewerCreator != null ? customTextViewerCreator.hashCode() : 0);
        result = 31 * result + (nestedName != null ? nestedName.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        return result;
    }
}
