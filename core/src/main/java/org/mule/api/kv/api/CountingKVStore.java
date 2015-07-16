/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.kv.api;

public interface CountingKVStore<V extends Number> extends KVStore<V>
{

    /**
     * Atomically produce:
     * {@code
     * V value = get(key);
     * V newValue = (value!=null ? value : 0) + delta;
     * put(key, newValue);
     * return newValue;
     * }
     * @param key
     * @param delta
     * @return The stored value after the increment
     */
    V addAndGet(String key, V delta);

    /**
     * Atomically produce:
     * {@code
     * V value = get(key);
     * put(key, (value!=null ? value : 0) + delta);
     * return value;
     * }
     * @param key
     * @param delta
     * @return The stored value after the increment
     */
    V getAndAdd(String key, V delta);

}
