/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.internal.capability.studio.editor;

import org.mule.extension.annotation.api.capability.Studio;
import org.mule.extension.api.introspection.declaration.DescribingContext;
import org.mule.extension.api.introspection.declaration.fluent.DeclarationDescriptor;
import org.mule.extension.api.introspection.property.StudioModelProperty;
import org.mule.module.extension.internal.model.AbstractAnnotatedModelEnricher;

/**
 * Created by pablocabrera on 11/18/15.
 */
//Rename Studio
public final class StudioModelEnricher extends AbstractAnnotatedModelEnricher
{

    @Override
    public void enrich(DescribingContext describingContext)
    {
        Studio studioStudio = extractAnnotation(describingContext.getDeclarationDescriptor().getDeclaration(), Studio.class);
        if (studioStudio == null || (!studioStudio.provided()))
        {
            DeclarationDescriptor descriptor = describingContext.getDeclarationDescriptor();
            descriptor.withModelProperty(StudioModelProperty.KEY, createStudioEditorModelProperty(studioStudio, descriptor));
        }
    }

    private StudioModelProperty createStudioEditorModelProperty(Studio studio, DeclarationDescriptor descriptor)
    {

        if (studio == null)
        {
            return new ImmutableStudioModelProperty("editors.xml", false);
        }
        else
        {
            return new ImmutableStudioModelProperty(studio.editorFileName(), studio.provided());
        }
    }

}
