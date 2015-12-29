/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.basic;

import org.mule.extension.annotation.api.Configurations;
import org.mule.extension.annotation.api.Extension;
import org.mule.extension.annotation.api.Operations;
import org.mule.extension.annotation.api.capability.Studio;
import org.mule.extension.annotation.api.capability.Xml;

@Extension(name = "basic", description = "Basic")
@Xml(schemaLocation = "http://www.mulesoft.org/schema/mule/basic", namespace = "basic", schemaVersion = "1.0")
@Operations({BasicOperations.class})
@Configurations({BasicConfig.class})
@Studio
public class BasicExtension
{

}
