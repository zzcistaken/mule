/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import org.mule.functional.junit4.ExtensionFunctionalTestCase;

public class JmsSubscriberTestCase extends ExtensionFunctionalTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "jms-subscriber-test-case";
    }


}
