/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.internal.capability.studio.editor;

import org.mule.extension.api.introspection.ConfigurationModel;
import org.mule.extension.api.introspection.ConnectionProviderModel;
import org.mule.extension.api.introspection.DataQualifier;
import org.mule.extension.api.introspection.DataType;
import org.mule.extension.api.introspection.ExpressionSupport;
import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.extension.api.introspection.OperationModel;
import org.mule.extension.api.introspection.ParameterModel;
import org.mule.extension.api.introspection.property.XmlModelProperty;
import org.mule.module.extension.internal.ExtensionProperties;
import org.mule.module.extension.internal.introspection.AbstractDataQualifierVisitor;
import org.mule.module.extension.internal.util.IntrospectionUtils;
import org.mule.module.extension.internal.util.NameUtils;
import org.mule.module.extension.studio.model.AbstractBaseEditorElement;
import org.mule.module.extension.studio.model.ConnectivityTesting;
import org.mule.module.extension.studio.model.EditorElementVisitorAdapter;
import org.mule.module.extension.studio.model.MetaDataBehaviour;
import org.mule.module.extension.studio.model.Namespace;
import org.mule.module.extension.studio.model.contribution.AbstractContributionEditorElement;
import org.mule.module.extension.studio.model.contribution.CloudConnector;
import org.mule.module.extension.studio.model.contribution.Container;
import org.mule.module.extension.studio.model.contribution.Flow;
import org.mule.module.extension.studio.model.contribution.Nested;
import org.mule.module.extension.studio.model.contribution.global.AbstractGlobalElement;
import org.mule.module.extension.studio.model.contribution.global.GlobalCloudConnector;
import org.mule.module.extension.studio.model.element.AbstractElementController;
import org.mule.module.extension.studio.model.element.AttributeCategory;
import org.mule.module.extension.studio.model.element.BaseChildEditorElement;
import org.mule.module.extension.studio.model.element.BaseFieldEditorElement;
import org.mule.module.extension.studio.model.element.BooleanEditor;
import org.mule.module.extension.studio.model.element.ChildElement;
import org.mule.module.extension.studio.model.element.DynamicEditor;
import org.mule.module.extension.studio.model.element.EditorRef;
import org.mule.module.extension.studio.model.element.EncodingEditor;
import org.mule.module.extension.studio.model.element.EnumEditor;
import org.mule.module.extension.studio.model.element.FileEditor;
import org.mule.module.extension.studio.model.element.Group;
import org.mule.module.extension.studio.model.element.IntegerEditor;
import org.mule.module.extension.studio.model.element.LongEditor;
import org.mule.module.extension.studio.model.element.Mode;
import org.mule.module.extension.studio.model.element.ModeSwitch;
import org.mule.module.extension.studio.model.element.NameEditor;
import org.mule.module.extension.studio.model.element.NoOperation;
import org.mule.module.extension.studio.model.element.Option;
import org.mule.module.extension.studio.model.element.PathEditor;
import org.mule.module.extension.studio.model.element.StringEditor;
import org.mule.module.extension.studio.model.element.UrlEditor;
import org.mule.module.extension.studio.model.element.macro.ElementControllerList;
import org.mule.module.extension.studio.model.element.macro.ElementControllerListOfMap;
import org.mule.module.extension.studio.model.element.macro.ElementControllerListOfPojo;
import org.mule.module.extension.studio.model.reference.GlobalRef;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;

/**
 * Created by pablocabrera on 11/18/15.
 */
public final class StudioGenerator
{

    public static final String ICONS_SMALL_EXTENSION_PATTERN = "icons/small/%s-extension-24x16.png";
    public static final String ICONS_LARGE_EXTENSION_PATTERN = "icons/large/%s-extension-48x32.png";
    public static final String CONNECTION_PROVIDER_ID = "connection-provider";
    private static final String DEFAULT_VERSIONS_RANGE = "[3.5.0,8.0.0]";
    public static final String CONNECTION_PROVIDER_GROUP_CAPTION = "Connection";
    public static final String CONNECTION_TAB_CAPTION = "Connection";
    public static final String CONNECTION_TAB_DESCRIPTION = "Configure the Connection Properties";

    private Map<OperationModel, AbstractContributionEditorElement> operations;
    private Map<String, Nested> nestedElements;
    private Map<ConfigurationModel, AbstractGlobalElement> configurations;
    private Map<ConnectionProviderModel, Nested> conectionProviders;

    private Namespace namespace;
    private ExtensionModel extensionModel;

    private StudioGenerator()
    {
        operations = new HashMap<>();
        configurations = new HashMap<>();
        conectionProviders = new HashMap<>();
        nestedElements = new HashMap<>();
    }


    public static StudioGenerator newStudioEditorGenerator(ExtensionModel extensionModel)
    {
        StudioGenerator generator = new StudioGenerator();
        generator.extensionModel = extensionModel;
        generator.namespace = new Namespace();

        return generator;
    }

    private boolean needsConnectionProviderPicker(ExtensionModel extensionModel)
    {
        return extensionModel.getConnectionProviders().size() > 1;

    }

    private void addConnectorProviderPicker(ExtensionModel extensionModel)
    {
        Nested connectionProviderPicker = new Nested();
        connectionProviderPicker.setLocalId(CONNECTION_PROVIDER_ID);
        Group group = new Group();
        group.setCaption(CONNECTION_PROVIDER_GROUP_CAPTION);
        group.setId("connectionProvider");
        DynamicEditor dynamicEditorsElement = new DynamicEditor();
        dynamicEditorsElement.setName("Provider");
        conectionProviders.values().forEach(provider -> {
            EditorRef editorRef = new EditorRef();
            editorRef.setName(provider.getCaption());
            editorRef.setId(namespace.getUrl() + "/" + provider.getLocalId());
            dynamicEditorsElement.getEditorReferences().add(editorRef);
        });
        group.getChilds().add(dynamicEditorsElement);
        connectionProviderPicker.getChildElements().add(group);
        namespace.getComponents().add(connectionProviderPicker);
    }

    private void addOperationParent(ExtensionModel extensionModel)
    {
        CloudConnector cloudConnector = new CloudConnector();
        cloudConnector.setCaption(getAbstractOperationParentValue(extensionModel));
        cloudConnector.setLocalId(getAbstractOperationParentValue(extensionModel));
        setIcons(cloudConnector);
        cloudConnector.setIsAbstract(true);
        cloudConnector.setVersions(DEFAULT_VERSIONS_RANGE);
        Group group = new Group();
        group.setId(getAbstractOperationParentValue(extensionModel));
        group.setCaption("Basic Settings");
        GlobalRef globalRef = new GlobalRef();
        globalRef.setRequired(true);
        globalRef.setName("config-ref");
        globalRef.setCaption("Extension Configuration");
        globalRef.setDescription("Specify which configuration to use for this invocation.");
        group.getChilds().add(globalRef);
        if (configurations.size() == 1)
        {
            globalRef.setRequiredType(namespace.getUrl() + '/' + configurations.values().iterator().next().getLocalId());
        }
        else
        {
            Optional<String> values = configurations.values().stream().map(x -> namespace.getUrl() + '/' + x.getLocalId()).reduce((x, y) -> x + ", " + y);
            globalRef.setRequiredType(String.format("$%s", values.get()));
        }

        AttributeCategory attributeCategory = new AttributeCategory();
        attributeCategory.setCaption("General");
        attributeCategory.setDescription("Configuration Properties");
        attributeCategory.getChilds().add(group);
        cloudConnector.getAttributeCategories().add(attributeCategory);
        namespace.getComponents().add(cloudConnector);
    }

    private String getAbstractOperationParentValue(ExtensionModel extensionModel)
    {
        return String.format("abstract%sGeneric", StringUtils.capitalize(extensionModel.getName()));
    }

    private void addOperationPicker(ExtensionModel extensionModel)
    {
        CloudConnector cloudConnector = new CloudConnector();
        cloudConnector.setDescription(extensionModel.getDescription());
        cloudConnector.setLocalId(extensionModel.getName() + "-extension");
        cloudConnector.setCaption(StringUtils.capitalize(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, extensionModel.getName())));
        setIcons(cloudConnector);
        cloudConnector.setVersions(DEFAULT_VERSIONS_RANGE);
        cloudConnector.setAliasId("org.mule.tooling.ui.modules.core.pattern." + extensionModel.getName());
        cloudConnector.setExtendsElement(String.format("%s/%s", namespace.getUrl(), getAbstractOperationParentValue(extensionModel)));
        AttributeCategory attributeCategory = new AttributeCategory();
        attributeCategory.setCaption("General");
        attributeCategory.setDescription("General.");
        cloudConnector.getAttributeCategories().add(attributeCategory);
        Group generalGroup = new Group();
        generalGroup.setId(extensionModel.getName() + "ConnectorGeneric");
        generalGroup.setCaption("Basic Settings");
        ModeSwitch modeSwitch = new ModeSwitch();
        modeSwitch.setAlwaysCombo(true);
        modeSwitch.setCaption("Operation");
        modeSwitch.setDescription("Operation.");
        modeSwitch.setName(extensionModel.getName() + " operations to execute");
        NoOperation noOp = new NoOperation();
        noOp.setConnectorName("processor");
        noOp.setAbstractElement(String.format("%s/%s", namespace.getUrl(), getAbstractOperationParentValue(extensionModel)));
        modeSwitch.getModes().add(noOp);
        operations.values().stream().sorted((o1, o2) -> o1.getLocalId().compareTo(o2.getLocalId())).forEach(operation -> {
            Mode mode = new Mode();
            mode.setId(String.format("%s/%s", namespace.getUrl(), operation.getLocalId()));
            mode.setLabel(operation.getCaption());
            modeSwitch.getModes().add(mode);
        });
        generalGroup.getChilds().add(modeSwitch);
        attributeCategory.getChilds().add(generalGroup);
        namespace.getComponents().add(cloudConnector);
    }

    private void addConnectionProvider(ConnectionProviderModel connectionProviderModel)
    {
        Nested globalConfig = new Nested();
        setIcons(globalConfig);
        globalConfig.setIsAbstract(false);
        globalConfig.setVersions(DEFAULT_VERSIONS_RANGE);
        globalConfig.setLocalId(connectionProviderModel.getName());
        globalConfig.setDescription(connectionProviderModel.getDescription());
        globalConfig.setCaption(StringUtils.capitalize(extensionModel.getName()) + ":" + StringUtils.capitalize(connectionProviderModel.getName()));
        connectionProviderModel.getParameterModels().forEach(parameterModel -> {
            addParameter(parameterModel, globalConfig.getChildElements());
        });
        namespace.getComponents().add(globalConfig);
        conectionProviders.put(connectionProviderModel, globalConfig);
    }

    private void addOperation(OperationModel operationModel)
    {
        if (hasNestedProcessorList(operationModel.getParameterModels()))
        {
            addFlow(operationModel);
        }
        else if (hasNestedProcessors(operationModel.getParameterModels()))
        {
            addContainer(operationModel);
        }
        else
        {
            addProcessor(operationModel);
        }
    }

    private void addFlow(OperationModel operationModel)
    {
        Flow container = new Flow();
        container.setCausesSplit(true);
        container.setIsAbstract(false);
        container.setMetaData(MetaDataBehaviour.STATIC);
        container.setVersions(DEFAULT_VERSIONS_RANGE);
        container.setLocalId(NameUtils.hyphenize(operationModel.getName()));
        container.setXmlname(NameUtils.hyphenize(operationModel.getName()));
        container.setDescription(operationModel.getDescription());
        container.setMetaData(MetaDataBehaviour.STATIC);
        container.setReturnType(getJavaType(operationModel.getReturnType()));
        container.setExtendsElement(String.format("%s/%s", namespace.getUrl(), getAbstractOperationParentValue(extensionModel)));
        container.setCaption(getCaption(operationModel.getName()));

        Group generalGroup = new Group();
        generalGroup.setId("general");
        generalGroup.setCaption("General");
        operationModel.getParameterModels().forEach(parameterModel -> {
            addParameter(parameterModel, generalGroup.getChilds());
        });
        AttributeCategory attributeCategory = new AttributeCategory();
        attributeCategory.setCaption("General");
        attributeCategory.setDescription("Configuration Properties");
        attributeCategory.getChilds().add(generalGroup);
        container.getAttributeCategories().add(attributeCategory);
        //operations.put(operationModel,container);
        namespace.getComponents().add(container);
    }

    private void addContainer(OperationModel operationModel)
    {
        Container container = new Container();
        container.setPathExpression("${index}");
        container.setAcceptedByElements("!http://www.mulesoft.org/schema/mule/core/mule");
        container.setIsAbstract(false);
        container.setTitleColor("168, 206, 226");
        container.setCaption(getCaption(operationModel.getName()));
        container.setMetaData(MetaDataBehaviour.STATIC);
        container.setVersions(DEFAULT_VERSIONS_RANGE);
        container.setLocalId(NameUtils.hyphenize(operationModel.getName()));
        container.setXmlname(NameUtils.hyphenize(operationModel.getName()));
        container.setDescription(operationModel.getDescription());
        container.setMetaData(MetaDataBehaviour.STATIC);
        container.setReturnType(getJavaType(operationModel.getReturnType()));
        container.setExtendsElement(String.format("%s/%s", namespace.getUrl(), getAbstractOperationParentValue(extensionModel)));
        Group generalGroup = new Group();
        generalGroup.setId("general");
        generalGroup.setCaption("General");
        operationModel.getParameterModels().forEach(parameterModel -> {
            addParameter(parameterModel, generalGroup.getChilds());
        });
        AttributeCategory attributeCategory = new AttributeCategory();
        attributeCategory.setCaption("General");
        attributeCategory.setDescription("Configuration Properties");
        attributeCategory.getChilds().add(generalGroup);
        container.getAttributeCategories().add(attributeCategory);
        //operations.put(operationModel,container);
        namespace.getComponents().add(container);
    }

    private String getCaption(String name)
    {
        return StringUtils.capitalize(StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(name),
                ' '
        ));
    }

    private void addProcessor(OperationModel operationModel)
    {
        CloudConnector cloudConnector = new CloudConnector();
        cloudConnector.setCaption(getCaption(operationModel.getName()));
        setIcons(cloudConnector);
        cloudConnector.setIsAbstract(true);
        cloudConnector.setVersions(DEFAULT_VERSIONS_RANGE);
        cloudConnector.setLocalId(NameUtils.hyphenize(operationModel.getName()));
        cloudConnector.setXmlname(NameUtils.hyphenize(operationModel.getName()));
        cloudConnector.setDescription(operationModel.getDescription());
        cloudConnector.setMetaData(MetaDataBehaviour.STATIC);
        cloudConnector.setReturnType(getJavaType(operationModel.getReturnType()));
        cloudConnector.setExtendsElement(String.format("%s/%s", namespace.getUrl(), getAbstractOperationParentValue(extensionModel)));

        Group generalGroup = new Group();
        generalGroup.setId("general");
        generalGroup.setCaption("General");
        AttributeCategory attributeCategory = new AttributeCategory();
        attributeCategory.setCaption("General");
        attributeCategory.setDescription("Configuration Properties");
        attributeCategory.getChilds().add(generalGroup);
        cloudConnector.getAttributeCategories().add(attributeCategory);

        operationModel.getParameterModels().forEach(parameterModel -> {
            if (isContentMetadataParameter(parameterModel.getName()))
            {
                Group mimeTypeGroup = getOrCreateMimeTypeGroup(cloudConnector.getAttributeCategories());
                addParameter(parameterModel, mimeTypeGroup.getChilds());
            }
            else
            {
                addParameter(parameterModel, generalGroup.getChilds());
            }
        });


        operations.put(operationModel, cloudConnector);
        namespace.getComponents().add(cloudConnector);
    }

    private Group getOrCreateMimeTypeGroup(List<AttributeCategory> attributeCategories)
    {
        for (AttributeCategory attributeCategory : attributeCategories)
        {
            if (attributeCategory.getId() == "mimeTypeAttributeCategory")
            {
                return (Group) attributeCategory.getChilds().get(0);
            }
        }
        AttributeCategory attributeCategory = new AttributeCategory();
        attributeCategory.setCaption("Mime Type");
        attributeCategory.setDescription("Mime Type Configuration");
        attributeCategory.setId("mimeTypeAttributeCategory");
        Group group = new Group();
        group.setId("mimeTypeGroup");
        group.setCaption("Mime Type Attributes");
        attributeCategory.getChilds().add(group);
        attributeCategories.add(attributeCategory);
        return group;
    }

    private boolean isContentMetadataParameter(String name)
    {
        return ExtensionProperties.ENCODING_PARAMETER_NAME.equals(name) || ExtensionProperties.MIME_TYPE_PARAMETER_NAME.equals(name);
    }


    private void addConfiguration(ConfigurationModel configurationModel)
    {
        GlobalCloudConnector globalConfig = new GlobalCloudConnector();
        setIcons(globalConfig);
        globalConfig.setIsAbstract(false);
        globalConfig.setVersions(DEFAULT_VERSIONS_RANGE);
        globalConfig.setLocalId(configurationModel.getName());
        globalConfig.setDescription(configurationModel.getDescription());
        globalConfig.setConnectivityTesting(ConnectivityTesting.OFF);
        globalConfig.setMetaData(MetaDataBehaviour.STATIC);
        globalConfig.setSupportsUserDefinedMetaData(false);
        globalConfig.setCaption(StringUtils.capitalize(extensionModel.getName()) + " " + configurationModel.getName());
        AttributeCategory attributeCategory = new AttributeCategory();
        attributeCategory.setCaption("General");
        attributeCategory.setDescription("Configuration Properties");
        NameEditor name = new NameEditor();
        name.setCaption("Name");
        name.setName("name");
        name.setDescription("Give a name to this configuration so it can be later referenced by config-ref.");
        name.setRequired(true);
        Group group = new Group();
        group.setCaption("Basic Settings");
        group.setId("basicGenericProperties");
        group.getChilds().add(name);
        attributeCategory.getChilds().add(group);
        Group generalGroup = new Group();
        generalGroup.setId("general");
        generalGroup.setCaption("General");

        configurationModel.getParameterModels().forEach(parameterModel -> {
            addParameter(parameterModel, generalGroup.getChilds());
        });

        attributeCategory.getChilds().add(generalGroup);
        globalConfig.getAttributeCategories().add(attributeCategory);
        addConnectionProviderTab(globalConfig);
        namespace.getComponents().add(globalConfig);
        configurations.put(configurationModel, globalConfig);
    }

    private void addConnectionProviderTab(GlobalCloudConnector globalConfig)
    {
        if (conectionProviders.isEmpty())
        {
            return;
        }
        AttributeCategory attributeCategory = new AttributeCategory();
        attributeCategory.setCaption(CONNECTION_TAB_CAPTION);
        attributeCategory.setDescription(CONNECTION_TAB_DESCRIPTION);

        ChildElement connectionProviderPicker = new ChildElement();
        connectionProviderPicker.setIndented(false);
        connectionProviderPicker.setInplace(true);
        int xmlOder = 10;
        connectionProviderPicker.setXmlOrder(xmlOder);
        if (conectionProviders.size() == 1)
        {
            connectionProviderPicker.setName(namespace.getUrl() + "/" + conectionProviders.values().iterator().next().getLocalId());
        }
        else
        {
            connectionProviderPicker.setName(namespace.getUrl() + "/connection-provider");
            connectionProviderPicker.setValuePersistence("org.mule.tooling.ui.modules.core.widgets.editors.dynamic.DynamicEditorValuesPreProcessor");
            connectionProviderPicker.setPersistenceTransformer("DefaultDynamicEditorPersistenceTransformer");
            Optional<String> values = conectionProviders.values().stream().map(x -> namespace.getUrl() + '/' + x.getLocalId()).reduce((x, y) -> x + "," + y);
            connectionProviderPicker.setEditorsIds(values.get());
        }

        Group generalGroup = new Group();
        generalGroup.setId("general");
        generalGroup.setCaption("General");
        generalGroup.getChilds().add(connectionProviderPicker);
        if (conectionProviders.size() > 1)
        {
            conectionProviders.values().stream().forEach( item->{
                ChildElement child = new ChildElement();
                child.setXmlOrder(xmlOder);
                child.setVisibleInDialog(false);
                child.setName(namespace.getUrl() + '/' + item.getLocalId());
                generalGroup.getChilds().add(child);
            });
        }
        attributeCategory.getChilds().add(generalGroup);
        globalConfig.getAttributeCategories().add(attributeCategory);
    }

    private boolean hasNestedProcessors(List<ParameterModel> parameterModels)
    {
        boolean hasNestedProcessor = false;
        for (ParameterModel param : parameterModels)
        {
            if (param.getType().getQualifier().equals(DataQualifier.OPERATION))
            {
                hasNestedProcessor = true;
                break;
            }
            else if (param.getType().getQualifier().equals(DataQualifier.LIST))
            {
                DataType[] genericTypes = param.getType().getGenericTypes();
                if (genericTypes != null && genericTypes.length == 1)
                {
                    if (genericTypes[0].getQualifier().equals(DataQualifier.OPERATION))
                    {
                        hasNestedProcessor = true;
                        break;
                    }
                }
            }
        }
        return hasNestedProcessor;
    }

    private boolean hasNestedProcessorList(List<ParameterModel> parameterModels)
    {
        boolean hasNestedProcessor = false;
        for (ParameterModel param : parameterModels)
        {
            if (param.getType().getQualifier().equals(DataQualifier.LIST))
            {
                DataType[] genericTypes = param.getType().getGenericTypes();
                if (genericTypes != null && genericTypes.length == 1)
                {
                    if (genericTypes[0].getQualifier().equals(DataQualifier.OPERATION))
                    {
                        hasNestedProcessor = true;
                        break;
                    }
                }
            }
        }
        return hasNestedProcessor;
    }

    private void addParameter(ParameterModel parameterModel, List<BaseChildEditorElement> container)
    {
        parameterModel.getType().getQualifier().accept(new AbstractDataQualifierVisitor()
        {

            @Override
            public void onBoolean()
            {

                BooleanEditor editor = new BooleanEditor();
                setCommonAttributes(editor, parameterModel);
                container.add(editor);
            }

            @Override
            public void onInteger()
            {
                IntegerEditor editor = new IntegerEditor();
                setCommonAttributes(editor, parameterModel);
                container.add(editor);
            }

            @Override
            public void onDouble()
            {
                StringEditor editor = new StringEditor();
                Class<?> javaType = parameterModel.getType().getRawType();
                editor.setJavaType(javaType.getTypeName());
                setCommonAttributes(editor, parameterModel);
                container.add(editor);
            }

            @Override
            public void onDecimal()
            {
                StringEditor editor = new StringEditor();
                setCommonAttributes(editor, parameterModel);
                Class<?> javaType = parameterModel.getType().getRawType();
                editor.setJavaType(javaType.getTypeName());
                container.add(editor);
            }

            @Override
            public void onString()
            {
                if (parameterModel.getName().equals(ExtensionProperties.MIME_TYPE_PARAMETER_NAME))
                {
                    EnumEditor editor = new EnumEditor();
                    setCommonAttributes(editor, parameterModel);
                    editor.setCaption("MIME Type");
                    List<String> optionStrings = Lists.newArrayList(Sets.newHashSet("text/plain", "text/css", "text/javascript", "text/xml", "text/xhtml", "text/html", "text/json", "text/csv", "image/jpeg",
                                                                                    "image/gif", "image/png", "application/json", "application/xml", "application/csv", "application/pdf", "application/x-compressed", "application/zip",
                                                                                    "multipart/x-zip", "binary/octet-stream"));

                    Collections.sort(optionStrings);
                    optionStrings.stream().forEach(option -> {
                        Option enumOption = new Option();
                        enumOption.setValue(option);
                        editor.getOptions().add(enumOption);
                    });
                    container.add(editor);
                }
                else if (parameterModel.getName().equals(ExtensionProperties.ENCODING_PARAMETER_NAME))
                {
                    EncodingEditor editor = new EncodingEditor();
                    setCommonAttributes(editor, parameterModel);
                    editor.setCaption("Encoding");
                    container.add(editor);
                }
                else
                {
                    StringEditor editor = new StringEditor();
                    setCommonAttributes(editor, parameterModel);
                    container.add(editor);
                }
            }

            @Override
            public void onLong()
            {
                LongEditor editor = new LongEditor();
                setCommonAttributes(editor, parameterModel);
                container.add(editor);
            }

            @Override
            public void onEnum()
            {
                EnumEditor editor = new EnumEditor();
                setCommonAttributes(editor, parameterModel);
                Class<?> javaType = parameterModel.getType().getRawType();
                editor.setJavaType(javaType.getTypeName());
                for (Object obj : javaType.getEnumConstants())
                {
                    Option option = new Option();
                    option.setValue(obj.toString());
                    editor.getOptions().add(option);
                }
                container.add(editor);
            }

            @Override
            public void onDateTime()
            {
                StringEditor editor = new StringEditor();
                setCommonAttributes(editor, parameterModel);
                Class<?> javaType = parameterModel.getType().getRawType();
                editor.setJavaType(javaType.getTypeName());
                container.add(editor);
            }

            @Override
            public void onPojo()
            {
                if (parameterModel.getType().getRawType().getName().equals("org.mule.module.http.api.requester.HttpRequesterConfig"))
                {
                    GlobalRef editorRef = new GlobalRef();
                    setCommonAttributes(editorRef, parameterModel);
                    editorRef.setRequiredType("http://www.mulesoft.org/schema/mule/http/request-config");
                    container.add(editorRef);
                }
                else
                {
                    Nested nestedElement = getOrCreateNestedElement(parameterModel);
                    ChildElement childElement = new ChildElement();
                    childElement.setInplace(true);
                    childElement.setAllowMultiple(false);
                    childElement.setCaption(getCaption(parameterModel.getName()));
                    childElement.setName(namespace.getUrl() + "/" + nestedElement.getLocalId());
                    childElement.setJavaType(parameterModel.getType().getRawType().getTypeName());
                    container.add(childElement);
                }

            }

            @Override
            public void onList()
            {
                if (parameterModel.getType().getGenericTypes().length == 1)
                {
                    parameterModel.getType().getGenericTypes()[0].getQualifier().accept(new AbstractDataQualifierVisitor()
                    {
                        @Override
                        public void onPojo()
                        {
                            ElementControllerListOfPojo elementControllerList = new ElementControllerListOfPojo();
                            setCommonAttributes(elementControllerList, parameterModel);
                            String hyphenizeName = NameUtils.hyphenize(parameterModel.getName());
                            elementControllerList.setLocalName(hyphenizeName);
                            elementControllerList.setItemName(NameUtils.singularize(hyphenizeName));
                            elementControllerList.setListName(hyphenizeName);
                            container.add(elementControllerList);
                        }

                        @Override
                        public void onMap()
                        {
                            ElementControllerListOfMap elementControllerList = new ElementControllerListOfMap();
                            setCommonAttributes(elementControllerList, parameterModel);
                            String hyphenizeName = NameUtils.hyphenize(parameterModel.getName());
                            elementControllerList.setLocalName(hyphenizeName);
                            elementControllerList.setItemName(NameUtils.singularize(hyphenizeName));
                            elementControllerList.setListName(hyphenizeName);
                            container.add(elementControllerList);
                        }

                        @Override
                        public void defaultOperation()
                        {
                            ElementControllerList elementControllerList = new ElementControllerList();
                            setCommonAttributes(elementControllerList, parameterModel);
                            String hyphenizeName = NameUtils.hyphenize(parameterModel.getName());
                            elementControllerList.setLocalName(hyphenizeName);
                            elementControllerList.setItemName(NameUtils.singularize(hyphenizeName));
                            elementControllerList.setListName(hyphenizeName);
                            container.add(elementControllerList);
                        }

                        @Override
                        public void onOperation()
                        {
                            //Ignore Nested Parameter List
                        }

                    });
                }
            }

            @Override
            public void onMap()
            {
                defaultOperation();
            }

            @Override
            public void onOperation()
            {
                defaultOperation();
            }

        });

    }

    private Nested getOrCreateNestedElement(ParameterModel parameterModel)
    {
        String fieldName = NameUtils.hyphenize(parameterModel.getName());
        if (nestedElements.containsKey(fieldName))
        {
            return nestedElements.get(fieldName);
        }
        Nested nestedElement = new Nested();
        nestedElement.setLocalId(fieldName);
        nestedElement.setXmlname(fieldName);
        nestedElement.setCaption("Configure " + parameterModel.getName());
        nestedElement.setDescription(parameterModel.getDescription());

        Group nestedGroup = new Group();
        nestedGroup.setId("id" + namespace.getPrefix() + parameterModel.getName());
        nestedGroup.setCaption(getCaption(parameterModel.getName()));
        nestedElements.put(fieldName, nestedElement);
        IntrospectionUtils.getParameterFields(parameterModel.getType().getRawType()).forEach(parameter -> {
                                                                                                 getEditorElement(parameter, nestedElement.getChildElements());
                                                                                             }
        );

        namespace.getComponents().add(nestedElement);
        return nestedElement;
    }

    private Nested getOrCreateNestedElement(Field parameterModel)
    {
        String fieldName = NameUtils.hyphenize(parameterModel.getName());
        if (nestedElements.containsKey(fieldName))
        {
            return nestedElements.get(fieldName);
        }
        Nested nestedElement = new Nested();
        nestedElement.setLocalId(fieldName);
        nestedElement.setXmlname(fieldName);
        nestedElement.setCaption("");
        nestedElement.setDescription("");
        nestedElements.put(fieldName, nestedElement);
        IntrospectionUtils.getParameterFields(parameterModel.getType()).forEach(parameter -> {
                                                                                    getEditorElement(parameter, nestedElement.getChildElements());
                                                                                }
        );

        namespace.getComponents().add(nestedElement);
        return nestedElement;
    }

    private void getEditorElement(Field field, final List<BaseChildEditorElement> childElements)
    {
        final DataType dataType = IntrospectionUtils.getFieldDataType(field);
        dataType.getQualifier().accept(new AbstractDataQualifierVisitor()
        {

            @Override
            public void onBoolean()
            {
                BooleanEditor editor = new BooleanEditor();
                setCommonAttributesFromDataType(field, editor, dataType);
                childElements.add(editor);
            }

            @Override
            public void onInteger()
            {
                IntegerEditor editor = new IntegerEditor();
                setCommonAttributesFromDataType(field, editor, dataType);
                childElements.add(editor);
            }

            @Override
            public void onDouble()
            {
                StringEditor editor = new StringEditor();
                editor.setJavaType(dataType.getRawType().getTypeName());
                setCommonAttributesFromDataType(field, editor, dataType);
                childElements.add(editor);
            }

            @Override
            public void onDecimal()
            {
                StringEditor editor = new StringEditor();
                editor.setJavaType(dataType.getRawType().getTypeName());
                setCommonAttributesFromDataType(field, editor, dataType);
                childElements.add(editor);
            }

            @Override
            public void onString()
            {
                StringEditor editor = new StringEditor();
                setCommonAttributesFromDataType(field, editor, dataType);
                childElements.add(editor);
            }

            @Override
            public void onLong()
            {
                LongEditor editor = new LongEditor();
                setCommonAttributesFromDataType(field, editor, dataType);
                childElements.add(editor);
            }

            @Override
            public void onEnum()
            {
                EnumEditor editor = new EnumEditor();
                setCommonAttributesFromDataType(field, editor, dataType);
                editor.setJavaType(dataType.getRawType().getTypeName());
                childElements.add(editor);
            }

            @Override
            public void onDateTime()
            {
                StringEditor editor = new StringEditor();
                editor.setJavaType(dataType.getRawType().getTypeName());
                setCommonAttributesFromDataType(field, editor, dataType);
                childElements.add(editor);
            }

            @Override
            public void onPojo()
            {
                Nested nested = getOrCreateNestedElement(field);
                ChildElement childElement = new ChildElement();
                childElement.setInplace(false);
                childElement.setAllowMultiple(false);
                childElement.setCaption(getCaption(nested.getCaption() + " Reference"));
                childElement.setName(namespace.getUrl() + "/" + nested.getLocalId());
                childElement.setJavaType(dataType.getRawType().getTypeName());
                childElements.add(childElement);
            }

            @Override
            public void onList()
            {
                if (dataType.getGenericTypes().length == 1)
                {
                    dataType.getGenericTypes()[0].getQualifier().accept(new AbstractDataQualifierVisitor()
                    {
                        @Override
                        public void onPojo()
                        {
                            ElementControllerListOfPojo elementControllerList = new ElementControllerListOfPojo();
                            setCommonAttributesFromDataType(field, elementControllerList, dataType);
                            String hyphenizeName = NameUtils.hyphenize(field.getName());
                            elementControllerList.setLocalName(hyphenizeName);
                            elementControllerList.setItemName(NameUtils.singularize(hyphenizeName));
                            elementControllerList.setListName(hyphenizeName);
                            childElements.add(elementControllerList);
                        }

                        @Override
                        public void onMap()
                        {
                            ElementControllerListOfMap elementControllerList = new ElementControllerListOfMap();
                            setCommonAttributesFromDataType(field, elementControllerList, dataType);
                            String hyphenizeName = NameUtils.hyphenize(field.getName());
                            elementControllerList.setLocalName(hyphenizeName);
                            elementControllerList.setItemName(NameUtils.singularize(hyphenizeName));
                            elementControllerList.setListName(hyphenizeName);
                            childElements.add(elementControllerList);
                        }

                        @Override
                        public void defaultOperation()
                        {
                            ElementControllerList elementControllerList = new ElementControllerList();
                            setCommonAttributesFromDataType(field, elementControllerList, dataType);
                            String hyphenizeName = NameUtils.hyphenize(field.getName());
                            elementControllerList.setLocalName(hyphenizeName);
                            elementControllerList.setItemName(NameUtils.singularize(hyphenizeName));
                            elementControllerList.setListName(hyphenizeName);
                            childElements.add(elementControllerList);
                        }

                    });
                }
            }
        });
    }


    private void setCommonAttributes(AbstractElementController editor, ParameterModel parameterModel)
    {
        editor.setCaption(getCaption(parameterModel.getName()));
        editor.setDescription(parameterModel.getDescription());
        editor.setRequired(parameterModel.isRequired());
        String javaType = getJavaType(parameterModel.getType());
        editor.setJavaType(javaType);
        editor.setLocalName(parameterModel.getName());
    }

    private String getJavaType(DataType type)
    {
        StringBuilder builder = new StringBuilder();
        type.getQualifier().accept(new AbstractDataQualifierVisitor()
        {

            @Override
            public void onList()
            {
                builder.append(type.getRawType().getName());
                builder.append("<");
                for (DataType innerType : type.getGenericTypes())
                {
                    builder.append(getJavaType(innerType));
                    builder.append(",");
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append(">");
            }

            @Override
            public void onMap()
            {
                builder.append(type.getRawType().getName());
                builder.append("<");
                for (DataType innerType : type.getGenericTypes())
                {
                    builder.append(getJavaType(innerType));
                    builder.append(",");
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append(">");
            }

            @Override
            protected void defaultOperation()
            {
                builder.append(type.getRawType().getName());
            }
        });
        return builder.toString();
    }

    private void setCommonAttributes(BaseFieldEditorElement editor, ParameterModel parameterModel)
    {
        editor.setCaption(getCaption(parameterModel.getName()));
        editor.setName(parameterModel.getName());
        editor.setDescription(parameterModel.getDescription());
        editor.setSupportsExpressions(ExpressionSupport.SUPPORTED.equals(parameterModel.getExpressionSupport()));
        editor.setRequired(parameterModel.isRequired());
        editor.accept(new EditorElementVisitorAdapter()
        {
            @Override
            public void visit(BooleanEditor booleanEditor)
            {
                if (parameterModel.getDefaultValue() != null)
                {
                    booleanEditor.setDefaultValue(Boolean.valueOf(parameterModel.getDefaultValue().toString()));
                }
            }

            @Override
            public void visit(EnumEditor enumEditor)
            {
                if (parameterModel.getDefaultValue() != null)
                {
                    enumEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
                }
            }

            @Override
            public void visit(FileEditor fileEditor)
            {
                if (parameterModel.getDefaultValue() != null)
                {
                    fileEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
                }
            }

            @Override
            public void visit(IntegerEditor integerEditor)
            {
                if (parameterModel.getDefaultValue() != null)
                {
                    integerEditor.setDefaultValue(Integer.valueOf(parameterModel.getDefaultValue().toString()));
                }
            }

            @Override
            public void visit(LongEditor longEditor)
            {
                if (parameterModel.getDefaultValue() != null)
                {
                    longEditor.setDefaultValue(Long.valueOf(parameterModel.getDefaultValue().toString()));
                }
            }

            @Override
            public void visit(NameEditor nameEditor)
            {
                if (parameterModel.getDefaultValue() != null)
                {
                    nameEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
                }
            }

            @Override
            public void visit(PathEditor pathEditor)
            {
                if (parameterModel.getDefaultValue() != null)
                {
                    pathEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
                }
            }

            @Override
            public void visit(StringEditor stringEditor)
            {
                if (parameterModel.getDefaultValue() != null)
                {
                    stringEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
                }
            }

            @Override
            public void visit(UrlEditor urlEditor)
            {
                if (parameterModel.getDefaultValue() != null)
                {
                    urlEditor.setDefaultValue(parameterModel.getDefaultValue().toString());
                }
            }

        });
    }

    private void setCommonAttributesFromDataType(Field field, BaseFieldEditorElement editor, final DataType dataType)
    {
        editor.setCaption(getCaption(field.getName()));
        editor.setName(field.getName());
        editor.setSupportsExpressions(false);
        editor.setRequired(false);
    }

    private void setCommonAttributesFromDataType(Field field, AbstractElementController editor, final DataType parameterModel)
    {
        editor.setCaption(getCaption(field.getName()));
        editor.setLocalName(field.getName());
        String javaType = getJavaType(parameterModel);
        editor.setJavaType(javaType);
        editor.setRequired(false);
    }

    private void setIcons(AbstractBaseEditorElement abstractBaseEditorElement)
    {
        abstractBaseEditorElement.setIcon(getSmallIconPath());
        abstractBaseEditorElement.setImage(getLargeIconPath());
    }

    private String getSmallIconPath()
    {
        return String.format(ICONS_SMALL_EXTENSION_PATTERN, NameUtils.hyphenize(extensionModel.getName()));
    }

    private String getLargeIconPath()
    {
        return String.format(ICONS_LARGE_EXTENSION_PATTERN, NameUtils.hyphenize(extensionModel.getName()));
    }

    public Namespace build()
    {
        //TODO if there is no XmlModelProperty?
        XmlModelProperty xmlProperty = extensionModel.getModelProperty(XmlModelProperty.KEY);
        namespace.setPrefix(xmlProperty.getNamespace());
        namespace.setUrl(xmlProperty.getSchemaLocation());

        extensionModel.getConnectionProviders().forEach(this::addConnectionProvider);
        if (needsConnectionProviderPicker(extensionModel))
        {
            this.addConnectorProviderPicker(extensionModel);
        }
        extensionModel.getConfigurationModels().forEach(this::addConfiguration);
        this.addOperationParent(extensionModel);
        extensionModel.getOperationModels().forEach(this::addOperation);
        this.addOperationPicker(extensionModel);
        return namespace;
    }

}
