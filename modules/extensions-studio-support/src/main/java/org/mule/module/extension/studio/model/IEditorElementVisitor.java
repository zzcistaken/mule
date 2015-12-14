/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model;

import org.mule.module.extension.studio.model.element.*;
import org.mule.module.extension.studio.model.element.library.Jar;
import org.mule.module.extension.studio.model.element.library.LibrarySet;
import org.mule.module.extension.studio.model.element.library.NativeLibrary;
import org.mule.module.extension.studio.model.global.CloudConnectorMessageSource;
import org.mule.module.extension.studio.model.global.Global;
import org.mule.module.extension.studio.model.global.GlobalCloudConnector;
import org.mule.module.extension.studio.model.global.GlobalEndpoint;
import org.mule.module.extension.studio.model.global.GlobalFilter;
import org.mule.module.extension.studio.model.global.GlobalTransformer;
import org.mule.module.extension.studio.model.reference.ContainerRef;
import org.mule.module.extension.studio.model.reference.FlowRef;
import org.mule.module.extension.studio.model.reference.GlobalRef;
import org.mule.module.extension.studio.model.reference.ReverseGlobalRef;

public interface IEditorElementVisitor
{

    void visit(CloudConnectorMessageSource cloudConnectorMessageSource);

    void visit(Global global);

    void visit(GlobalCloudConnector globalCloudConnector);

    void visit(GlobalEndpoint globalEndpoint);

    void visit(GlobalFilter globalFilter);

    void visit(GlobalTransformer globalTransformer);

    void visit(AttributeCategory attributeCategory);

    void visit(BooleanEditor booleanEditor);

    void visit(Button button);

    void visit(Case caseEditorElement);

    void visit(ChildElement childElement);

    void visit(ClassNameEditor classNameEditor);

    void visit(Custom custom);

    void visit(DoubleEditor doubleEditor);

    void visit(Dummy dummy);

    void visit(DateTimeEditor datetime);

    void visit(DynamicEditor dynamicEditor);

    void visit(EditorRef editorRef);

    void visit(ElementControllerList elementControllerList);

    void visit(ElementControllerListNoExpression elementControllerListNoExpression);

    void visit(ElementControllerListOfMap elementControllerListOfMap);

    void visit(ElementControllerListOfPojo elementControllerListOfPojo);

    void visit(ElementControllerMap elementControllerMap);

    void visit(ElementControllerMapNoExpression elementControllerMapNoExpression);

    void visit(ElementQuery elementQuery);

    void visit(EncodingEditor encodingEditor);

    void visit(EnumEditor enumEditor);

    void visit(FileEditor fileEditor);

    void visit(FixedAttribute fixedAttribute);

    void visit(Group group);

    void visit(Horizontal horizontal);

    void visit(IntegerEditor integerEditor);

    void visit(LabelElement labelElement);

    void visit(ListEditor listEditor);

    void visit(LongEditor longEditor);

    void visit(Mode mode);

    void visit(ModeSwitch modeSwitch);

    void visit(NameEditor nameEditor);

    void visit(NativeLibrary nativeLibrary);

    void visit(NoOperation noOperation);

    void visit(Option option);

    void visit(PasswordEditor passwordEditor);

    void visit(PathEditor pathEditor);

    void visit(RadioBoolean radioBoolean);

    void visit(Regexp regexp);

    void visit(RequiredLibraries requiredLibraries);

    void visit(ResourceEditor resource);

    void visit(SoapInterceptor soapInterceptor);

    void visit(StringEditor stringEditor);

    void visit(StringMap stringMap);

    void visit(SwitchCase switchCase);

    void visit(TextEditor textEditor);

    void visit(TimeEditor timeEditor);

    void visit(TypeChooser typeChooser);

    void visit(UrlEditor urlEditor);

    void visit(UseMetaData useMetaData);

    void visit(Jar jar);

    void visit(LibrarySet librarySet);

    void visit(ContainerRef containerRef);

    void visit(FlowRef flowRef);

    void visit(GlobalRef globalRef);

    void visit(ReverseGlobalRef reverseGlobalRef);

    void visit(Alternative alternative);

    void visit(CloudConnector cloudConnector);

    void visit(Component component);

    void visit(Connector connector);

    void visit(Container container);

    void visit(Endpoint endpoint);

    void visit(Filter filter);

    void visit(Flow flow);

    void visit(GraphicalContainer graphicalContainer);

    void visit(Keyword keyword);

    void visit(KeywordSet keywordSet);

    void visit(LocalRef localRef);

    void visit(MultiSource multiSource);

    void visit(Namespace namespace);

    void visit(MultiTypeChooser multiTypeChooser);

    void visit(Nested nested);

    void visit(NestedContainer nestedContainer);

    void visit(Pattern pattern);

    void visit(Radio radio);

    void visit(RequiredSetAlternatives requiredSetAlternatives);

    void visit(Transformer transformer);

    void visit(Wizard wizard);

}
