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

/**
 * Created by pablocabrera on 12/9/15.
 */
@XmlRootElement(name = "multi-type-chooser")
public class MultiTypeChooser extends BaseFieldEditorElement
{

    private String associatedConfig;
    private String keySeparator;

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
    public String getKeySeparator()
    {
        return keySeparator;
    }

    public void setKeySeparator(String keySeparator)
    {
        this.keySeparator = keySeparator;
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {

    }
}
