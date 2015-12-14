/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.firstextension;

import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.ParameterGroup;

import java.util.List;

public class ExtensionListOperations
{

    @Operation
    public List<String> listStringOperation(List<String> params1)
    {
        return params1;
    }

    @Operation
    public List<SimplePojo> listParameterGroupPojoOperation(@ParameterGroup List<SimplePojo> pojos)
    {
        return pojos;
    }

    @Operation
    public List<SimplePojo> listPojoOperation(List<SimplePojo> pojos)
    {
        return pojos;
    }

    @Operation
    public List<AnotherSimplePojo> listPojoComplexOperation(List<AnotherSimplePojo> myComplexPojos)
    {
        return null;
    }

    //@Operation
    //public void interfaceOperation(MyInterface callback){
    //
    //}
}
