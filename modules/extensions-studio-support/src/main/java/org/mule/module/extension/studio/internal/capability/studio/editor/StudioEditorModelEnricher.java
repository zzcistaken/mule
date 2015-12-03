/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.internal.capability.studio.editor;

import org.mule.extension.annotation.api.capability.StudioEditor;
import org.mule.extension.api.introspection.declaration.DescribingContext;
import org.mule.extension.api.introspection.declaration.fluent.DeclarationDescriptor;
import org.mule.extension.api.introspection.property.StudioEditorModelProperty;
import org.mule.module.extension.internal.model.AbstractAnnotatedModelEnricher;

/**
 * Created by pablocabrera on 11/18/15.
 */
public final class StudioEditorModelEnricher extends AbstractAnnotatedModelEnricher
{

    @Override
    public void enrich(DescribingContext describingContext)
    {
        StudioEditor studioEditor = extractAnnotation(describingContext.getDeclarationDescriptor().getDeclaration(), StudioEditor.class);
        if (studioEditor != null)
        {
            DeclarationDescriptor descriptor = describingContext.getDeclarationDescriptor();
            descriptor.withModelProperty(StudioEditorModelProperty.KEY, createStudioEditorModelProperty(studioEditor, descriptor));
        }
    }

    private StudioEditorModelProperty createStudioEditorModelProperty(StudioEditor studioEditor, DeclarationDescriptor descriptor)
    {

        return new ImmutableStudioEditorModelProperty(studioEditor.fileName());
    }

}
