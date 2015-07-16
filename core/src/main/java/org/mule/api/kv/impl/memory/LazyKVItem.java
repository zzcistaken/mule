/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.kv.impl.memory;

import org.mule.api.kv.api.KVItem;
import org.mule.api.kv.api.KVStore;

/**
*
*/
public class LazyKVItem<V> implements KVItem<V>
{

    private final String key;
    private final KVStore<V> store;
    private V value;

    public LazyKVItem(String key, KVStore<V> store)
    {
        this.key = key;
        this.store = store;
    }

    @Override
    public String key()
    {
        return key;
    }

    @Override
    public V value()
    {

        if (value==null) {
            value = store.get(key);
        }
        return value;
    }
}
