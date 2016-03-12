/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.http.api;

import org.mule.extension.api.annotation.Configurations;
import org.mule.extension.api.annotation.Extension;
import org.mule.extension.api.annotation.Operations;
import org.mule.extension.api.annotation.Sources;
import org.mule.extension.api.annotation.connector.Providers;

@Extension(name = "HttpExt Connector", description = "Connector to receive and send HTTP requests")
@Configurations({HttpRequesterConfig.class, HttpListenerConfig.class})
@Providers({HttpRequesterProvider.class})
@Operations({HttpRequesterOperations.class})
@Sources(HttpListener.class)
public class HttpExtConnector
{

}