package org.mule.api.kv.impl;

import org.mule.api.kv.api.KVItem;
import org.mule.api.kv.api.KVStore;

import java.util.Iterator;

/**
 *
 */
public class KVStoreSupport<V> implements KVStore<V>
{
    private KVStore<StoreItem<V>> baseStore;

    @Override
    public V put(String key, V value)
    {
        V previousValue = null;
        StoreItem<V> item = baseStore.get(key);
        if (item==null)
        {
            item = new StoreItem<>(key, value);
        }
        else
        {
            previousValue = item.value;
            item.value = value;
            item.lastUpdate = System.nanoTime();
        }
        baseStore.put(key, item);
        return previousValue;
    }

    @Override
    public boolean has(String key)
    {
        return baseStore.has(key);
    }

    @Override
    public V get(String key)
    {
        StoreItem<V> item = baseStore.get(key);
        if (item!=null)
        {
            item.lastUpdate = System.nanoTime();
            baseStore.put(key, item);
            return item.value;
        }
        return null;
    }

    @Override
    public V del(String key)
    {
        StoreItem<V> item = baseStore.del(key);
        return item!=null ? item.value : null;
    }

    @Override
    public long itemsCount()
    {
        return baseStore.itemsCount();
    }

    @Override
    public boolean isEmpty()
    {
        return baseStore.isEmpty();
    }

    @Override
    public void clear()
    {
        baseStore.clear();
    }

    @Override
    public Iterable<KVItem<V>> items()
    {
        return new Iterable<KVItem<V>>()
        {
            @Override
            public Iterator<KVItem<V>> iterator()
            {
                final Iterator<KVItem<StoreItem<V>>> it = baseStore.items().iterator();
                return new Iterator<KVItem<V>>()
                {
                    @Override
                    public boolean hasNext()
                    {
                        return it.hasNext();
                    }

                    @Override
                    public KVItem<V> next()
                    {
                        final KVItem<StoreItem<V>> item = it.next();
                        item.value().lastAccess = System.nanoTime();
                        baseStore.put(item.key(), item.value());
                        return new KVItem<V>()
                        {
                            @Override
                            public String key()
                            {
                                return item.key();
                            }

                            @Override
                            public V value()
                            {
                                return item.value().value;
                            }
                        };
                    }

                    @Override
                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    private class StoreItem<V>
    {
        V value;
        long creationTime;
        long lastUpdate;
        long lastAccess;
        long keySize;

        StoreItem(String key, V value)
        {
            this.value = value;
            this.keySize = key.length();
            creationTime = System.nanoTime();
            lastUpdate = creationTime;
            lastAccess = 0;
        }
    }
}

