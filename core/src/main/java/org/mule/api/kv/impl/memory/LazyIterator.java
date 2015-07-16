/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.kv.impl.memory;

import org.mule.api.kv.api.KVItem;
import org.mule.api.kv.api.KVStore;

import java.util.Iterator;

/**
*
*/
class LazyIterator<V> implements Iterator<KVItem<V>>
{

    private final Iterator<String> keysIterator;
    private final KVStore<V> store;

    public LazyIterator(Iterator<String> keysIterator, KVStore<V> store)
    {
        this.keysIterator = keysIterator;
        this.store = store;
    }

    @Override
    public boolean hasNext()
    {
        return keysIterator.hasNext();
    }

    @Override
    public KVItem<V> next()
    {
        return new LazyKVItem(keysIterator.next(), store);
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
