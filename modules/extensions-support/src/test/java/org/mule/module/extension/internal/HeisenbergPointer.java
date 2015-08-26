/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal;

import static org.mule.module.extension.HeisenbergExtension.EXTENSION_DESCRIPTION;
import static org.mule.module.extension.HeisenbergExtension.EXTENSION_NAME;
import static org.mule.module.extension.HeisenbergExtension.NAMESPACE;
import static org.mule.module.extension.HeisenbergExtension.SCHEMA_LOCATION;
import static org.mule.module.extension.HeisenbergExtension.SCHEMA_VERSION;
import org.mule.extension.annotations.Configurations;
import org.mule.extension.annotations.Operations;
import org.mule.extension.annotations.capability.Xml;
import org.mule.module.extension.HeisenbergExtension;
import org.mule.module.extension.HeisenbergOperations;
import org.mule.module.extension.MoneyLaunderingOperation;

@org.mule.extension.annotations.Extension(name = EXTENSION_NAME, description = EXTENSION_DESCRIPTION)
@Xml(schemaLocation = SCHEMA_LOCATION, namespace = NAMESPACE, schemaVersion = SCHEMA_VERSION)
@Configurations(HeisenbergExtension.class)
@Operations({HeisenbergOperations.class, MoneyLaunderingOperation.class})
public class HeisenbergPointer extends HeisenbergExtension
{

}
