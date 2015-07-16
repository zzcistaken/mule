/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.kv;

import org.mule.api.kv.api.KVStore;

/**
 *
 */
interface KVStoreProvider
{

    /**
     * Creates a new store based on the received configuration
     * @param storeConfiguration Configuration details
     * @return A new store based on the available implementations and the required configuration
     */
    KVStore create(KVStoreConfiguration storeConfiguration);
}
