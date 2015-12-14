/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.firstextension;

import org.mule.extension.annotation.api.Operation;
import org.mule.extension.annotation.api.ParameterGroup;

public class ExtensionOperations
{

    @Operation
    public String stringOperation(String param1, String param2)
    {
        return param1 + " & " + param2;
    }

    @Operation
    public int primitiveTypesOperation(int a, int b)
    {
        return a + b;
    }

    @Operation
    public SimplePojo parameterGroupPojoOperation(@ParameterGroup SimplePojo pojo)
    {
        return pojo;
    }

    @Operation
    public SimplePojo pojoOperation(SimplePojo pojo)
    {
        return pojo;
    }

    //@Operation
    //public AnotherSimplePojo pojoComplexOperation(AnotherSimplePojo myComplexPojo){
    //	return null;
    //}
    //TODO Add when interfaces are supported
    //@Operation
    //public void interfaceOperation(MyInterface callback){
    //
    //}
}
