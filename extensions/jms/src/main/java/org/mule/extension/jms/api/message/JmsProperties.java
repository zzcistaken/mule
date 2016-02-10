/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.api.message;

import java.util.Map;

public interface JmsProperties extends Map<String, Object>
{

    Map<String, Object> getUserProperties();
    Map<String, Object> getJmsProperties();
    Map<String, Object> getJmsxProperties();

}
