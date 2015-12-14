/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.internal.capability.studio.editor;

import org.mule.extension.api.introspection.property.StudioEditorModelProperty;

/**
 * Created by pablocabrera on 11/18/15.
 */
public class ImmutableStudioEditorModelProperty implements StudioEditorModelProperty
{

    private final String fileName;

    public ImmutableStudioEditorModelProperty(String fileName)
    {
        this.fileName = fileName;
    }

    @Override
    public String getFileName()
    {
        return fileName;
    }
}
