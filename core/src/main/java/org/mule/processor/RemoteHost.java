/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.processor;

public class RemoteHost
{
    private boolean successfullyConnected;
    private String userAgent;

    public RemoteHost(boolean successfullyConnected, String userAgent)
    {
        this.successfullyConnected = successfullyConnected;
        this.userAgent = userAgent;
    }

    public boolean isSuccessfullyConnected()
    {
        return successfullyConnected;
    }

    public String getUserAgent()
    {
        return userAgent;
    }
}
