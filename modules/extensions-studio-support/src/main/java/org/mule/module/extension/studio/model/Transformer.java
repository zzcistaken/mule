/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Transformer extends AbstractPaletteComponent
{

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }
}
