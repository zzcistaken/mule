/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.kv.api;

import java.util.Date;

/**
 *
 */
public interface MonitorableKVStore<V extends Object> extends KVStore<V>
{

    // Store based information
    Date creation();
    Date lastUpdate();
    Date lastAccess();
    Long usedSpace();

    // Item based information
    Date insertion(String key);
    Date lastUpdate(String key);
    Date lastAccess(String key);
    Date remotion(String key);
    Long usedSpace(String key);
}

