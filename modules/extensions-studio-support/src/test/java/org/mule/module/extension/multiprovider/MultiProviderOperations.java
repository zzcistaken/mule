/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.multiprovider;

import org.mule.api.NestedProcessor;
import org.mule.extension.annotation.api.ContentMetadataParameters;
import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.param.UseConfig;
import org.mule.extension.api.runtime.ContentMetadata;
import org.mule.module.extension.multiconfig.AbstractConfig;

import java.util.List;

/**
 * Created by pablocabrera on 11/26/15.
 */
public class MultiProviderOperations
{

    @Operation
    public void dummyOperation()
    {

    }

    @Operation
    public void config1Operation(@UseConfig AbstractConfig configuration)
    {

    }

    @Operation
    public void nestedSingle(NestedProcessor firstProcessor)
    {

    }

    @Operation
    public void nestedMultiple(List<NestedProcessor> processors)
    {

    }

    @Operation
    @ContentMetadataParameters
    public String contentTypeOperation(String param1, ContentMetadata contentMetadata)
    {
        return param1;
    }
}
