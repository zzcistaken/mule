/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.kv.api;

import java.util.Collection;

public interface KVStoreFactory<V extends Object>
{

    /**
     * Returns the store associated to received name, if no store exists a new one will be created
     */
    public KVStore<V> get(String name);

    /**
     * Returns the set of all store names registered
     */
    public Collection<String> getNames();

    /**
     * Deletes the referenced store, all its key/value pairs will be lost.
     */
    public void remove(String name);

}
