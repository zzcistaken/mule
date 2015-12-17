/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.firstextension;

import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.extension.annotation.api.Extension;
import org.mule.extension.annotation.api.Operations;
import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.ParameterGroup;
import org.mule.extension.annotation.api.capability.Editor;
import org.mule.extension.annotation.api.capability.Xml;
import org.mule.extension.annotation.api.connector.Providers;

import java.util.List;

@Extension(name = "first", description = "Mule First Extension")
@Operations({ExtensionOperations.class, ExtensionOperationsWithDefaults.class, ExtensionOperationsWithOptional.class, ExtensionListOperations.class})
@Providers(FirstExtensionConnectionProvider.class)
@Xml(schemaLocation = "http://www.mulesoft.org/schema/mule/first", namespace = "first", schemaVersion = "3.7")
@Editor
public class FirstExtension implements Initialisable
{


    @Parameter
    private boolean hasItems;

    @Parameter
    private List<String> sampleNames;

    @ParameterGroup
    @Parameter
    private SimplePojo parameterGroup;

    @Parameter
    private AnotherSimplePojo anotherSimplePojo;

    public void initialise() throws InitialisationException
    {

    }

    public boolean getHasItems()
    {
        return hasItems;
    }

    public void setHasItems(boolean hasItems)
    {
        this.hasItems = hasItems;
    }

    public List<String> getSampleNames()
    {
        return sampleNames;
    }

    public void setSampleNames(List<String> sampleNames)
    {
        this.sampleNames = sampleNames;
    }

    public SimplePojo getParameterGroup()
    {
        return parameterGroup;
    }

    public void setParameterGroup(SimplePojo parameterGroup)
    {
        this.parameterGroup = parameterGroup;
    }

    public AnotherSimplePojo getAnotherSimplePojo()
    {
        return anotherSimplePojo;
    }

    public void setAnotherSimplePojo(AnotherSimplePojo anotherSimplePojo)
    {
        this.anotherSimplePojo = anotherSimplePojo;
    }
}
