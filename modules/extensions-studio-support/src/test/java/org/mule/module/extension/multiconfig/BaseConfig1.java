/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.multiconfig;

import org.mule.extension.annotation.api.Configuration;
import org.mule.extension.annotation.api.Parameter;

/**
 * Created by pablocabrera on 11/26/15.
 */
@Configuration(name = "config1")
public class BaseConfig1 extends AbstractConfig
{
    @Parameter
    private String hash;

    public String getHash()
    {
        return hash;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }
}
