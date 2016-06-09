/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring.dsl.processor.xml;

import static java.util.Arrays.asList;
import org.mule.runtime.config.spring.dsl.api.xml.XmlNamespaceInfo;
import org.mule.runtime.config.spring.dsl.api.xml.XmlNamespaceInfoProvider;

import java.util.Collection;

/**
 * Provides the core namespace XML information.
 *
 * @since 4.0
 */
public class CoreXmlNamespaceInfoProvider implements XmlNamespaceInfoProvider
{

    public static final String CORE_NAMESPACE_NAME = "mule";

    @Override
    public Collection<XmlNamespaceInfo> getXmlNamespacesInfo()
    {
        return asList(new XmlNamespaceInfo()
        {
            @Override
            public String getNamespaceUriPrefix()
            {
                return "http://www.mulesoft.org/schema/mule/core";
            }

            @Override
            public String getNamespace()
            {
                return CORE_NAMESPACE_NAME;
            }
        });
    }
}
