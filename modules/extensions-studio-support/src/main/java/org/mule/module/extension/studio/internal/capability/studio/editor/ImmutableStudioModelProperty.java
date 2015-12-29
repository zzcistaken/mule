/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.internal.capability.studio.editor;

import org.mule.extension.api.introspection.property.StudioModelProperty;

/**
 * Immutable implementation of {@link StudioModelProperty}
 *
 * @since 4.0
 */
public final class ImmutableStudioModelProperty implements StudioModelProperty
{

    private final String editorfileName;
    private final boolean provided;
    public ImmutableStudioModelProperty(String fileName, boolean provided)
    {
        this.editorfileName = fileName;
        this.provided = provided;
     }

    @Override
    public String getEditorFileName()
    {
        return editorfileName;
    }

    @Override
    public boolean isProvided()
    {
        return provided;
    }
}
