/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.multiconfig;

import org.mule.extension.annotation.api.Configurations;
import org.mule.extension.annotation.api.Extension;
import org.mule.extension.annotation.api.Operations;
import org.mule.extension.annotation.api.capability.StudioEditor;
import org.mule.extension.annotation.api.capability.Xml;
import org.mule.extension.annotation.api.connector.Providers;

/**
 * Created by pablocabrera on 11/26/15.
 */
@Extension(name = "multi-config")
@Xml(schemaLocation = "http://www.mulesoft.org/schema/mule/multi-config", namespace = "multi-config", schemaVersion = "3.7")
@Operations(MultiConfigOperations.class)
@Configurations({BaseConfig1.class,BaseConfig2.class})
@Providers({MultiConfigProvider.class})
@StudioEditor
public class MultiConfigExtension
{

}
