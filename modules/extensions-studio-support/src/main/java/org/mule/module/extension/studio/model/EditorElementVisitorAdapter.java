/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model;

import org.mule.module.extension.studio.model.contribution.CloudConnector;
import org.mule.module.extension.studio.model.contribution.Component;
import org.mule.module.extension.studio.model.contribution.Connector;
import org.mule.module.extension.studio.model.contribution.Container;
import org.mule.module.extension.studio.model.contribution.Endpoint;
import org.mule.module.extension.studio.model.contribution.Filter;
import org.mule.module.extension.studio.model.contribution.Flow;
import org.mule.module.extension.studio.model.contribution.GraphicalContainer;
import org.mule.module.extension.studio.model.contribution.MultiSource;
import org.mule.module.extension.studio.model.contribution.Nested;
import org.mule.module.extension.studio.model.contribution.NestedContainer;
import org.mule.module.extension.studio.model.contribution.Pattern;
import org.mule.module.extension.studio.model.contribution.Transformer;
import org.mule.module.extension.studio.model.contribution.Wizard;
import org.mule.module.extension.studio.model.element.*;
import org.mule.module.extension.studio.model.element.library.Jar;
import org.mule.module.extension.studio.model.element.library.LibrarySet;
import org.mule.module.extension.studio.model.element.library.NativeLibrary;
import org.mule.module.extension.studio.model.contribution.CloudConnectorMessageSource;
import org.mule.module.extension.studio.model.contribution.global.Global;
import org.mule.module.extension.studio.model.contribution.global.GlobalCloudConnector;
import org.mule.module.extension.studio.model.contribution.global.GlobalEndpoint;
import org.mule.module.extension.studio.model.contribution.global.GlobalFilter;
import org.mule.module.extension.studio.model.contribution.global.GlobalTransformer;
import org.mule.module.extension.studio.model.element.library.RequiredLibraries;
import org.mule.module.extension.studio.model.element.macro.ElementControllerList;
import org.mule.module.extension.studio.model.element.macro.ElementControllerListNoExpression;
import org.mule.module.extension.studio.model.element.macro.ElementControllerListOfMap;
import org.mule.module.extension.studio.model.element.macro.ElementControllerListOfPojo;
import org.mule.module.extension.studio.model.element.macro.ElementControllerMap;
import org.mule.module.extension.studio.model.element.macro.ElementControllerMapNoExpression;
import org.mule.module.extension.studio.model.element.macro.ElementQuery;
import org.mule.module.extension.studio.model.reference.ContainerRef;
import org.mule.module.extension.studio.model.reference.FlowRef;
import org.mule.module.extension.studio.model.reference.GlobalRef;
import org.mule.module.extension.studio.model.reference.LocalRef;
import org.mule.module.extension.studio.model.reference.ReverseGlobalRef;

/**
 * Created by pablocabrera on 11/24/15.
 */
public class EditorElementVisitorAdapter implements IEditorElementVisitor
{

    protected void defaultOperation(AbstractEditorElement element)
    {

    }

    @Override
    public void visit(CloudConnectorMessageSource cloudConnectorMessageSource)
    {
        defaultOperation(cloudConnectorMessageSource);
    }

    @Override
    public void visit(Global global)
    {
        defaultOperation(global);
    }

    @Override
    public void visit(GlobalCloudConnector globalCloudConnector)
    {
        defaultOperation(globalCloudConnector);
    }

    @Override
    public void visit(GlobalEndpoint globalEndpoint)
    {
        defaultOperation(globalEndpoint);
    }

    @Override
    public void visit(GlobalFilter globalFilter)
    {
        defaultOperation(globalFilter);
    }

    @Override
    public void visit(GlobalTransformer globalTransformer)
    {
        defaultOperation(globalTransformer);
    }

    @Override
    public void visit(AttributeCategory attributeCategory)
    {
        defaultOperation(attributeCategory);
    }

    @Override
    public void visit(BooleanEditor booleanEditor)
    {
        defaultOperation(booleanEditor);
    }

    @Override
    public void visit(Button button)
    {
        defaultOperation(button);
    }

    @Override
    public void visit(Case caseEditorElement)
    {
        defaultOperation(caseEditorElement);
    }

    @Override
    public void visit(ChildElement childElement)
    {
        defaultOperation(childElement);
    }

    @Override
    public void visit(ClassNameEditor classNameEditor)
    {
        defaultOperation(classNameEditor);
    }

    @Override
    public void visit(Custom custom)
    {
        defaultOperation(custom);
    }

    @Override
    public void visit(DoubleEditor doubleEditor)
    {
        defaultOperation(doubleEditor);
    }

    @Override
    public void visit(Dummy dummy)
    {
        defaultOperation(dummy);
    }

    @Override
    public void visit(DateTimeEditor datetime)
    {
        defaultOperation(datetime);
    }

    @Override
    public void visit(DynamicEditor dynamicEditor)
    {
        defaultOperation(dynamicEditor);
    }

    @Override
    public void visit(EditorRef editorRef)
    {
        defaultOperation(editorRef);
    }

    @Override
    public void visit(ElementControllerList elementControllerList)
    {
        defaultOperation(elementControllerList);
    }

    @Override
    public void visit(ElementControllerListNoExpression elementControllerListNoExpression)
    {
        defaultOperation(elementControllerListNoExpression);
    }

    @Override
    public void visit(ElementControllerListOfMap elementControllerListOfMap)
    {
        defaultOperation(elementControllerListOfMap);
    }

    @Override
    public void visit(ElementControllerListOfPojo elementControllerListOfPojo)
    {
        defaultOperation(elementControllerListOfPojo);
    }

    @Override
    public void visit(ElementControllerMap elementControllerMap)
    {
        defaultOperation(elementControllerMap);
    }

    @Override
    public void visit(ElementControllerMapNoExpression elementControllerMapNoExpression)
    {
        defaultOperation(elementControllerMapNoExpression);
    }

    @Override
    public void visit(ElementQuery elementQuery)
    {
        defaultOperation(elementQuery);
    }

    @Override
    public void visit(EncodingEditor encodingEditor)
    {
        defaultOperation(encodingEditor);
    }

    @Override
    public void visit(EnumEditor enumEditor)
    {
        defaultOperation(enumEditor);
    }

    @Override
    public void visit(FileEditor fileEditor)
    {
        defaultOperation(fileEditor);
    }

    @Override
    public void visit(FixedAttribute fixedAttribute)
    {
        defaultOperation(fixedAttribute);
    }

    @Override
    public void visit(Group group)
    {
        defaultOperation(group);
    }

    @Override
    public void visit(Horizontal horizontal)
    {
        defaultOperation(horizontal);
    }

    @Override
    public void visit(IntegerEditor integerEditor)
    {
        defaultOperation(integerEditor);
    }

    @Override
    public void visit(LabelElement labelElement)
    {
        defaultOperation(labelElement);
    }

    @Override
    public void visit(ListEditor listEditor)
    {
        defaultOperation(listEditor);
    }

    @Override
    public void visit(LongEditor longEditor)
    {
        defaultOperation(longEditor);
    }

    @Override
    public void visit(Mode mode)
    {
        defaultOperation(mode);
    }

    @Override
    public void visit(ModeSwitch modeSwitch)
    {
        defaultOperation(modeSwitch);
    }

    @Override
    public void visit(NameEditor nameEditor)
    {
        defaultOperation(nameEditor);
    }

    @Override
    public void visit(NativeLibrary nativeLibrary)
    {
        defaultOperation(nativeLibrary);
    }

    @Override
    public void visit(NoOperation noOperation)
    {
        defaultOperation(noOperation);
    }

    @Override
    public void visit(Option option)
    {
        defaultOperation(option);
    }

    @Override
    public void visit(PasswordEditor passwordEditor)
    {
        defaultOperation(passwordEditor);
    }

    @Override
    public void visit(PathEditor pathEditor)
    {
        defaultOperation(pathEditor);
    }

    @Override
    public void visit(RadioBoolean radioBoolean)
    {
        defaultOperation(radioBoolean);
    }

    @Override
    public void visit(Regexp regexp)
    {
        defaultOperation(regexp);
    }

    @Override
    public void visit(RequiredLibraries requiredLibraries)
    {
        defaultOperation(requiredLibraries);
    }

    @Override
    public void visit(ResourceEditor resource)
    {
        defaultOperation(resource);
    }

    @Override
    public void visit(SoapInterceptor soapInterceptor)
    {
        defaultOperation(soapInterceptor);
    }

    @Override
    public void visit(StringEditor stringEditor)
    {
        defaultOperation(stringEditor);
    }

    @Override
    public void visit(StringMap stringMap)
    {
        defaultOperation(stringMap);
    }

    @Override
    public void visit(SwitchCase switchCase)
    {
        defaultOperation(switchCase);
    }

    @Override
    public void visit(TextEditor textEditor)
    {
        defaultOperation(textEditor);
    }

    @Override
    public void visit(TimeEditor timeEditor)
    {
        defaultOperation(timeEditor);
    }

    @Override
    public void visit(TypeChooser typeChooser)
    {
        defaultOperation(typeChooser);
    }

    @Override
    public void visit(UrlEditor urlEditor)
    {
        defaultOperation(urlEditor);
    }

    @Override
    public void visit(UseMetaData useMetaData)
    {
        defaultOperation(useMetaData);
    }

    @Override
    public void visit(Jar jar)
    {
        defaultOperation(jar);
    }

    @Override
    public void visit(LibrarySet librarySet)
    {
        defaultOperation(librarySet);
    }

    @Override
    public void visit(ContainerRef containerRef)
    {
        defaultOperation(containerRef);
    }

    @Override
    public void visit(FlowRef flowRef)
    {
        defaultOperation(flowRef);
    }

    @Override
    public void visit(GlobalRef globalRef)
    {
        defaultOperation(globalRef);
    }

    @Override
    public void visit(ReverseGlobalRef reverseGlobalRef)
    {
        defaultOperation(reverseGlobalRef);
    }

    @Override
    public void visit(Alternative alternative)
    {
        defaultOperation(alternative);
    }

    @Override
    public void visit(CloudConnector cloudConnector)
    {
        defaultOperation(cloudConnector);
    }

    @Override
    public void visit(Component component)
    {
        defaultOperation(component);
    }

    @Override
    public void visit(Connector connector)
    {
        defaultOperation(connector);
    }

    @Override
    public void visit(Container container)
    {
        defaultOperation(container);
    }

    @Override
    public void visit(Endpoint endpoint)
    {
        defaultOperation(endpoint);
    }

    @Override
    public void visit(Filter filter)
    {
        defaultOperation(filter);
    }

    @Override
    public void visit(Flow flow)
    {
        defaultOperation(flow);
    }

    @Override
    public void visit(GraphicalContainer graphicalContainer)
    {
        defaultOperation(graphicalContainer);
    }

    @Override
    public void visit(Keyword keyword)
    {
        defaultOperation(keyword);
    }

    @Override
    public void visit(KeywordSet keywordSet)
    {
        defaultOperation(keywordSet);
    }

    @Override
    public void visit(LocalRef localRef)
    {
        defaultOperation(localRef);
    }

    @Override
    public void visit(MultiSource multiSource)
    {
        defaultOperation(multiSource);
    }

    @Override
    public void visit(Namespace namespace)
    {
        defaultOperation(namespace);
    }

    @Override
    public void visit(MultiTypeChooser multiTypeChooser)
    {
        defaultOperation(multiTypeChooser);
    }

    @Override
    public void visit(Nested nested)
    {
        defaultOperation(nested);
    }

    @Override
    public void visit(NestedContainer nestedContainer)
    {
        defaultOperation(nestedContainer);
    }

    @Override
    public void visit(Pattern pattern)
    {
        defaultOperation(pattern);
    }

    @Override
    public void visit(Radio radio)
    {
        defaultOperation(radio);
    }

    @Override
    public void visit(RequiredSetAlternatives requiredSetAlternatives)
    {
        defaultOperation(requiredSetAlternatives);
    }

    @Override
    public void visit(Transformer transformer)
    {
        defaultOperation(transformer);
    }

    @Override
    public void visit(Wizard wizard)
    {
        defaultOperation(wizard);
    }
}
