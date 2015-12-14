/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element;

import org.mule.module.extension.studio.model.AbstractEditorElement;
import org.mule.module.extension.studio.model.ModeType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({AbstractElementController.class, AttributeCategory.class, BaseFieldEditorElement.class, Button.class, Group.class, Horizontal.class, SwitchCase.class})
public abstract class BaseChildEditorElement extends AbstractEditorElement
{

    private String caption;
    // TODO: See if description can be removed from Button.class
    private String description;
    private ModeType mode;

    @XmlAttribute
    public ModeType getMode()
    {
        return mode;
    }

    public void setMode(ModeType mode)
    {
        this.mode = mode;
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
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        return "BaseEditorElement [caption=" + caption + ", description=" + description + "]";
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

        BaseChildEditorElement that = (BaseChildEditorElement) o;

        if (caption != null ? !caption.equals(that.caption) : that.caption != null)
        {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null)
        {
            return false;
        }
        return mode == that.mode;

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        return result;
    }
}
