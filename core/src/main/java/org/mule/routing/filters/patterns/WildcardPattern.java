/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.routing.filters.patterns;

public abstract class WildcardPattern
{
    protected String parsedPattern;

    protected boolean caseSensitive;

    public WildcardPattern(String parsedPattern)
    {
        this.parsedPattern = parsedPattern;
    }

    public void setCaseSensitive(boolean caseSensitive)
    {
        this.caseSensitive = caseSensitive;
    }

    public abstract boolean evaluate(Object candidate);
}
