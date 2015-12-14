/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.basic;

import org.mule.extension.annotation.api.Configuration;
import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.module.http.api.requester.HttpRequesterConfig;

@Configuration(name = "config")
public class BasicConfig
{

    @Parameter
    private MyComplexObject myComplexObjectConfigurable;

    @Parameter
    private HttpRequesterConfig requesterConfig;

    @Parameter
    @Optional
    private HttpRequesterConfig requesterConfigOptional;

    public MyComplexObject getMyComplexObjectConfigurable()
    {
        return myComplexObjectConfigurable;
    }

    public void setMyComplexObjectConfigurable(MyComplexObject myComplexObjectConfigurable)
    {
        this.myComplexObjectConfigurable = myComplexObjectConfigurable;
    }

    public HttpRequesterConfig getRequesterConfig()
    {
        return requesterConfig;
    }

    public void setRequesterConfig(HttpRequesterConfig requesterConfig)
    {
        this.requesterConfig = requesterConfig;
    }

    public HttpRequesterConfig getRequesterConfigOptional()
    {
        return requesterConfigOptional;
    }

    public void setRequesterConfigOptional(HttpRequesterConfig requesterConfigOptional)
    {
        this.requesterConfigOptional = requesterConfigOptional;
    }

}
