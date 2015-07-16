/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.kv.api;

import java.util.List;

public interface ListingKVStore<V extends Object> extends KVStore<List<V>>
{

    /**
     * Atomically produce:
     * {@code
     * List<V> value = get(key);
     * value.add(item);
     * return value;
     * }
     * taking into account the case when the list does not previously exist
     * @param key
     * @param amount
     * @return The stored value after the increment
     */
    List<V> append(String key, V item);

    /**
     * Atomically produce:
     * {@code
     * List<V> value = get(key);
     * V item = value.remove(value.size()-1);
     * return item;
     * }
     * taking into account the case when the list does not previously exist or is empty
     * @param key
     * @param amount
     * @return The stored value after the increment
     */
    V remove(String key);

}
