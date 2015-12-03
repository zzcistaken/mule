/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.multiconfig;

/**
 * Created by pablocabrera on 11/26/15.
 */
public class MultiConfigConnection
{
    private boolean connected = true;

    public void disconnect()
    {
        connected = false;
    }

    public boolean isConnected()
    {
        return connected;
    }
}
