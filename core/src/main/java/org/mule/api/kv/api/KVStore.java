/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.kv.api;

/**
 * Basic interface for all key-value based object stores.
 * Keys are always Strings.
 * Values must be of a particular type.
 * @param <V> The class for all the values.
 */
public interface KVStore<V>
{

    /**
     * Inserts or updates a key with a specific value.
     * @param key The String to be used as a key to access the stored value
     * @param value The value to store
     * @return the previously associated value to the received key, or null if the key was not associated to any value
     */
    V put(String key, V value);

    /**
     * Checks for a specific key
     * @param key
     * @return true is the key exists in the store
     */
    boolean has(String key);

    /**
     * Retrieve the value associated to a particular key
     * @param key
     * @return The associated value or null if the key does not exist in the store
     */
    V get(String key);

    /**
     * Removes the key from the store
     * @param key
     * @return the previously associated value to the received key, or null if the key was not associated to any value
     */
    V del(String key);

    /**
     * @return The count of items stored
     */
    long itemsCount();

    /**
     * @return {link #itemsCount}==0
     */
    boolean isEmpty();

    /**
     * Removes all elements from the store
     */
    void clear();

    /**
     * @return An Iterable over all store keys
     */
    Iterable<KVItem<V>> items();
}


