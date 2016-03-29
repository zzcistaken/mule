/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.routing.filters.patterns;

/**
 * Represents patterns with no wildcards present (e.g.: fox).
 */
public class NoWildcardPattern extends StringWildcardPattern
{

    public NoWildcardPattern(String parsedPattern)
    {
        super(parsedPattern);
    }

    @Override
    protected boolean evaluate(String candidate)
    {
        return parsedPattern.equals(candidate);
    }
}
