/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.internal.capability.studio.editor;

import org.mule.extension.api.introspection.ExpressionSupport;
import org.mule.extension.api.introspection.ParameterModel;
import org.mule.module.extension.studio.model.EditorElementVisitorAdapter;
import org.mule.module.extension.studio.model.element.BaseFieldEditorElement;
import org.mule.module.extension.studio.model.element.BooleanEditor;
import org.mule.module.extension.studio.model.element.EnumEditor;
import org.mule.module.extension.studio.model.element.FileEditor;
import org.mule.module.extension.studio.model.element.IntegerEditor;
import org.mule.module.extension.studio.model.element.LongEditor;
import org.mule.module.extension.studio.model.element.NameEditor;
import org.mule.module.extension.studio.model.element.PathEditor;
import org.mule.module.extension.studio.model.element.StringEditor;
import org.mule.module.extension.studio.model.element.UrlEditor;

import com.google.common.base.CaseFormat;

import org.apache.commons.lang.StringUtils;

/**
 * Created by pablocabrera on 11/30/15.
 */
public class FieldEditorFactory
{
    public static BaseFieldEditorElement generateFieldFrom(ParameterModel parameterModel){

           return null;
    }
    private void setCommonAttributes(BaseFieldEditorElement editor, ParameterModel parameterModel)
    {
        editor.setCaption(StringUtils.capitalize(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameterModel.getName())));
        editor.setName(parameterModel.getName());
        editor.setDescription(parameterModel.getDescription());
        editor.setSupportsExpressions(ExpressionSupport.SUPPORTED.equals(parameterModel.getExpressionSupport()));
        editor.setRequired(parameterModel.isRequired());
        if(parameterModel.getDefaultValue()!=null)
        {
            configureDefault(editor, parameterModel);
        }
    }

    /**
     * Sets the default value according to the editor type. The parameterModel must have a default value
     * @param editor
     * @param parameterModel
     */
    private void configureDefault(BaseFieldEditorElement editor, final ParameterModel parameterModel)
    {
        editor.accept(new EditorElementVisitorAdapter()
        {
            @Override
            public void visit(BooleanEditor booleanEditor)
            {
                booleanEditor.setDefaultValue(Boolean.valueOf(parameterModel.getDefaultValue().toString()));
            }

            @Override
            public void visit(EnumEditor enumEditor)
            {
                enumEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
            }

            @Override
            public void visit(FileEditor fileEditor)
            {
                fileEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
            }

            @Override
            public void visit(IntegerEditor integerEditor)
            {
                integerEditor.setDefaultValue(Integer.valueOf(parameterModel.getDefaultValue().toString()));
            }

            @Override
            public void visit(LongEditor longEditor)
            {
                longEditor.setDefaultValue(Long.valueOf(parameterModel.getDefaultValue().toString()));
            }

            @Override
            public void visit(NameEditor nameEditor)
            {
                nameEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
            }

            @Override
            public void visit(PathEditor pathEditor)
            {
                pathEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
            }

            @Override
            public void visit(StringEditor stringEditor)
            {
                stringEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
            }

            @Override
            public void visit(UrlEditor urlEditor)
            {
                urlEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
            }

        });
    }
}
