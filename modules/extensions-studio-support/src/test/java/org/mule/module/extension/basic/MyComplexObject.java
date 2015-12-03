/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.extension.basic;

import org.mule.extension.annotation.api.Parameter;

public class MyComplexObject {

    /**
     * My value
     */
	@Parameter	//TODO I should not need to specify this to get a parameter
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}