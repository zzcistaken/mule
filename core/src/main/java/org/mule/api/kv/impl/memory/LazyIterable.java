package org.mule.api.kv.impl.memory;

import org.mule.api.kv.api.KVItem;
import org.mule.api.kv.api.KVStore;

import java.util.Iterator;

/**
*
*/
public class LazyIterable<V> implements Iterable<KVItem<V>>
{

    private final Iterator<KVItem<V>> it;

    public LazyIterable(Iterator<String> keysIterator, KVStore<V> store)
    {
        it = new LazyIterator(keysIterator, store);
    }

    @Override
    public Iterator<KVItem<V>> iterator()
    {
        return it;
    }
}
