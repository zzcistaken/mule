/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.routing.filters.patterns;

import org.mule.util.ClassUtils;

/**
 * Represents the deprecated payload based filtering pattern (e.g.: java.lang.Throwable+).
 */
@Deprecated
public class ClassNameWildcardPattern extends WildcardPattern
{

    public ClassNameWildcardPattern(String pattern)
    {
        super(pattern);
    }

    @Override
    public boolean evaluate(Object candidate)
    {
        try
        {
            Class<?> theClass = ClassUtils.loadClass(parsedPattern, this.getClass());
            if (!(candidate instanceof String))
            {
                if (theClass.isInstance(candidate))
                {
                    return true;
                }
            }
            else if (theClass.isAssignableFrom(ClassUtils.loadClass(candidate.toString(), this.getClass())))
            {
                return true;
            }
        }
        catch (ClassNotFoundException e)
        {
        }
        return false;
    }

}
