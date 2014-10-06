/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.vm.test;

import org.mule.api.store.ObjectStoreException;
import org.mule.api.store.QueueStore;
import org.mule.util.store.SimpleMemoryObjectStore;

import java.io.Serializable;

public class CustomObjectStore<T extends Serializable> extends SimpleMemoryObjectStore<T> implements QueueStore<T>
{
    public static int count;

    public CustomObjectStore()
    {
        super();
    }

    @Override
    protected void doStore(Serializable key, T value) throws ObjectStoreException
    {
        count++;
        super.doStore(key, value);
    }
}
