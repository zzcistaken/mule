/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring.dsl.processor.xml;

import org.mule.runtime.config.spring.dsl.model.ComponentModel;
import org.mule.runtime.config.spring.dsl.processor.ConfigLine;

import org.w3c.dom.Node;

/**
 * Handler for adding and removing custom XML attributes from config files.
 */
public class XmlCustomAttributeHandler
{
    public static final String NAMESPACE_URI = "NAMESPACE_URI";
    public static final String XML_NODE = "XML_NODE";
    public static final String CONFIG_FILE_NAME = "CONFIG_FILE_NAME";

    public static ConfigLineCustomAttributeStore to(ConfigLine.Builder builder)
    {
        return new ConfigLineCustomAttributeStore(builder);
    }

    public static ConfigLineCustomAttributeRetrieve from(ConfigLine configLine)
    {
        return new ConfigLineCustomAttributeRetrieve(configLine);
    }

    public static ComponentCustomAttributeStore to(ComponentModel.Builder builder)
    {
        return new ComponentCustomAttributeStore(builder);
    }

    public static ComponentCustomAttributeRetrieve from(ComponentModel componentModel)
    {
        return new ComponentCustomAttributeRetrieve(componentModel);
    }

    public static class ConfigLineCustomAttributeStore
    {

        private final ConfigLine.Builder builder;

        private ConfigLineCustomAttributeStore(ConfigLine.Builder builder)
        {
            this.builder = builder;
        }

        public ConfigLineCustomAttributeStore addNode(Node node)
        {
            this.builder.addCustomAttribute(XML_NODE, node);
            this.builder.addCustomAttribute(NAMESPACE_URI, node.getNamespaceURI());
            return this;
        }
    }

    public static class ConfigLineCustomAttributeRetrieve
    {
        private final ConfigLine configLine;

        private ConfigLineCustomAttributeRetrieve(ConfigLine configLine)
        {
            this.configLine = configLine;
        }

        public Node getNode()
        {
            return (Node) this.configLine.getCustomAttributes().get(XML_NODE);
        }
    }

    public static class ComponentCustomAttributeStore
    {

        private final ComponentModel.Builder builder;

        private ComponentCustomAttributeStore(ComponentModel.Builder builder)
        {
            this.builder = builder;
        }

        public ComponentCustomAttributeStore addNode(Node node)
        {
            this.builder.addCustomAttribute(XML_NODE, node);
            this.builder.addCustomAttribute(NAMESPACE_URI, node.getNamespaceURI());
            return this;
        }

        public ComponentCustomAttributeStore addConfigFileName(String configFileName)
        {
            this.builder.addCustomAttribute(CONFIG_FILE_NAME, configFileName);
            return this;
        }
    }

    public static class ComponentCustomAttributeRetrieve
    {
        private final ComponentModel componentModel;

        private ComponentCustomAttributeRetrieve(ComponentModel componentModel)
        {
            this.componentModel = componentModel;
        }

        public String getNamespaceUri()
        {
            return (String) this.componentModel.getCustomAttributes().get(NAMESPACE_URI);
        }

        public String getConfigFileName()
        {
            return (String) this.componentModel.getCustomAttributes().get(CONFIG_FILE_NAME);
        }

        public Node getNode()
        {
            return (Node) this.componentModel.getCustomAttributes().get(XML_NODE);
        }
    }

}
