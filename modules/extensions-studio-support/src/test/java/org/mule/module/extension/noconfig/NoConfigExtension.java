/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.noconfig;

import org.mule.extension.annotation.api.Extension;
import org.mule.extension.annotation.api.Operations;
import org.mule.extension.annotation.api.capability.Studio;
import org.mule.extension.annotation.api.capability.Xml;
import org.mule.extension.annotation.api.connector.Providers;

/**
 * Created by pablocabrera on 11/26/15.
 */
@Extension(name = "no-config")
@Xml(schemaLocation = "http://www.mulesoft.org/schema/mule/no-config", namespace = "no-config", schemaVersion = "3.7")
@Operations(NoConfigOperations.class)
@Providers({NoConfigProvider.class})
@Studio
public class NoConfigExtension
{

}
