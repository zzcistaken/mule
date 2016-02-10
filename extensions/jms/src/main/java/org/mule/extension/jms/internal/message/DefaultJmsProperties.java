/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.message;

import org.mule.extension.jms.api.message.JmsProperties;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DefaultJmsProperties implements JmsProperties
{
    private Map<String, Object> allPropertiesMap = new HashMap<>();
    private Map<String, Object> userProperties = new HashMap<>();
    private Map<String, Object> jmsProperties = new HashMap<>();
    private Map<String, Object> jmsxProperties = new HashMap<>();

    public DefaultJmsProperties(Map<String, Object> messageProperties)
    {
        allPropertiesMap.putAll(messageProperties);
        allPropertiesMap = Collections.unmodifiableMap(allPropertiesMap);
        for (Entry<String, Object> entry : messageProperties.entrySet())
        {
            Map<String, Object> mapToStoreEntry = userProperties;
            if (entry.getKey().startsWith("JMSX"))
            {
                mapToStoreEntry = jmsxProperties;
            }
            else if (entry.getKey().startsWith("JMS"))
            {
                mapToStoreEntry = jmsProperties;
            }
            mapToStoreEntry.put(entry.getKey(), entry.getValue());
        }
        userProperties = Collections.unmodifiableMap(userProperties);
        jmsProperties = Collections.unmodifiableMap(jmsProperties);
        jmsxProperties = Collections.unmodifiableMap(jmsxProperties);
    }

    @Override
    public Map<String, Object> getUserProperties()
    {
        return userProperties;
    }

    @Override
    public Map<String, Object> getJmsProperties()
    {
        return jmsProperties;
    }

    @Override
    public Map<String, Object> getJmsxProperties()
    {
        return jmsxProperties;
    }

    @Override
    public int size()
    {
        return allPropertiesMap.size();
    }

    @Override
    public boolean isEmpty()
    {
        return allPropertiesMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return allPropertiesMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return allPropertiesMap.containsValue(value);
    }

    @Override
    public Object get(Object key)
    {
        return allPropertiesMap.get(key);
    }

    @Override
    public Object put(String key, Object value)
    {
        return allPropertiesMap.put(key, value);
    }

    @Override
    public Object remove(Object key)
    {
        return allPropertiesMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m)
    {
        allPropertiesMap.putAll(m);
    }

    @Override
    public void clear()
    {
        allPropertiesMap.clear();
    }

    @Override
    public Set<String> keySet()
    {
        return allPropertiesMap.keySet();
    }

    @Override
    public Collection<Object> values()
    {
        return allPropertiesMap.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet()
    {
        return allPropertiesMap.entrySet();
    }

    @Override
    public boolean equals(Object o)
    {
        return allPropertiesMap.equals(o);
    }

    @Override
    public int hashCode()
    {
        return allPropertiesMap.hashCode();
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue)
    {
        return allPropertiesMap.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Object> action)
    {
        allPropertiesMap.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> function)
    {
        allPropertiesMap.replaceAll(function);
    }

    @Override
    public Object putIfAbsent(String key, Object value)
    {
        return allPropertiesMap.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value)
    {
        return allPropertiesMap.remove(key, value);
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue)
    {
        return allPropertiesMap.replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(String key, Object value)
    {
        return allPropertiesMap.replace(key, value);
    }

    @Override
    public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction)
    {
        return allPropertiesMap.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction)
    {
        return allPropertiesMap.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction)
    {
        return allPropertiesMap.compute(key, remappingFunction);
    }

    @Override
    public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction)
    {
        return allPropertiesMap.merge(key, value, remappingFunction);
    }
}
