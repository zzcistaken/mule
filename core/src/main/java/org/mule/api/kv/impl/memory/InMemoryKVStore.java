/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.kv.impl.memory;

import org.mule.api.kv.api.KVItem;
import org.mule.api.kv.api.KVStore;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;

import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

/**
 *
 */
public class InMemoryKVStore<V> implements KVStore<V>, Initialisable, Disposable
{
    private ConcurrentMap<String, V> map;

    @Override
    public void initialise() throws InitialisationException
    {
        map = Maps.newConcurrentMap();
    }

    @Override
    public void dispose()
    {
        map.clear();
        map = null;
    }

    @Override
    public V put(String key, V value)
    {
        return map.put(key, value);
    }

    @Override
    public boolean has(String key)
    {
        return map.containsKey(key);
    }

    @Override
    public V get(String key)
    {
        return map.get(key);
    }

    @Override
    public V del(String key)
    {
        return map.remove(key);
    }

    @Override
    public long itemsCount()
    {
        return map.size();
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public void clear()
    {
        map.clear();
    }

    @Override
    public Iterable<KVItem<V>> items()
    {
        return new LazyIterable(map.keySet().iterator(), this);
    }

}
