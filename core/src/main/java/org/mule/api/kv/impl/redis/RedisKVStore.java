package org.mule.api.kv.impl.redis;

import org.mule.api.kv.api.KVItem;
import org.mule.api.kv.api.KVStore;
import org.mule.api.kv.impl.memory.LazyKVItem;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;

import java.util.Iterator;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;

/**
 *
 */
public class RedisKVStore implements KVStore<String>, Initialisable, Disposable
{

    private static final String CURSOR_START = "0";
    private static final String CURSOR_END = "0";

    private Jedis jedis;

    @Override
    public void initialise() throws InitialisationException
    {
        jedis = new Jedis("localhost");
    }

    @Override
    public void dispose()
    {
        jedis.close();
    }

    @Override
    public String put(String key, String value)
    {
        return jedis.set(key, value);
    }

    @Override
    public boolean has(String key)
    {
        return jedis.exists(key);
    }

    @Override
    public String get(String key)
    {
        return jedis.get(key);
    }

    @Override
    public String del(String key)
    {
        String curValue = jedis.get(key);
        jedis.del(key);
        return curValue;
    }

    @Override
    public long itemsCount()
    {
        return jedis.dbSize();
    }

    @Override
    public boolean isEmpty()
    {
        return itemsCount()==0;
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<KVItem<String>> items()
    {
        return new RedisIterable(jedis.scan(CURSOR_START), this);
    }

    private class RedisIterable implements Iterable<KVItem<String>>
    {

        private final RedisKVStore store;
        private ScanResult<String> scanResult;

        public RedisIterable(ScanResult<String> scanResult, RedisKVStore store)
        {
            this.scanResult = scanResult;
            this.store = store;
        }

        @Override
        public Iterator<KVItem<String>> iterator()
        {
            return new RedisIterator(scanResult, store);
        }
    }

    private class RedisIterator implements Iterator<KVItem<String>>
    {

        private final RedisKVStore store;
        private Iterator<String> iterator;
        private ScanResult<String> scanResult;

        public RedisIterator(ScanResult<String> scanResult, RedisKVStore store)
        {
            this.scanResult = scanResult;
            this.store = store;
            iterator = scanResult.getResult().iterator();
        }

        @Override
        public boolean hasNext()
        {
            if (iterator.hasNext())
            {
                return true;
            }
            scanResult = jedis.scan(scanResult.getStringCursor());
            if (scanResult.getStringCursor().equals(CURSOR_END))
            {
                return false;
            }
            iterator = scanResult.getResult().iterator();
            return iterator.hasNext();
        }

        @Override
        public KVItem<String> next()
        {
            return new LazyKVItem<>(iterator.next(), store);
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
