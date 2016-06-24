/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transformer.types;

import java.util.List;
import java.util.Objects;

/**
 * Defines a List collection type with item type information
 *
 * @since 3.0
 */
public class ListDataType<T> extends CollectionDataType<T>
{
    public ListDataType()
    {
        super(List.class);
    }

    public ListDataType(Class type, String mimeType)
    {
        super(List.class, type, mimeType);
    }

    public ListDataType(Class type)
    {
        super(List.class, type);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }
        if (obj.getClass() != getClass())
        {
            return false;
        }
        CollectionDataType other = (CollectionDataType) obj;

        return Objects.equals(type, other.type)
               && Objects.equals(collectionType, other.collectionType)
               && Objects.equals(mimeType, other.mimeType);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(type, collectionType, mimeType);
    }
}
