/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.deployer;

import org.mule.deployer.descriptor.ApplicationDescriptor;

import java.io.IOException;

/**
 * Discovers application descriptor and settings.
 */
//TODO(pablo.kraan): OSGi - find a better name for this class
public interface AppBloodhound
{

    /**
     * Sniff around and get me the app descriptor!
     */
    ApplicationDescriptor fetch(String appName) throws IOException;
}
