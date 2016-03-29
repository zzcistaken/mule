/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.routing.filters.patterns;

public abstract class StringWildcardPattern extends WildcardPattern
{

    public StringWildcardPattern(String parsedPattern)
    {
        super(parsedPattern);
    }

    public boolean evaluate(Object object)
    {
        String candidate = object.toString();
        if (!caseSensitive)
        {
            candidate = candidate.toLowerCase();
        }
        return evaluate(candidate);
    }

    protected abstract boolean evaluate(String candidate);

}
