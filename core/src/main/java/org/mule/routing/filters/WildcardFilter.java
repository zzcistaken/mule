/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.routing.filters;

import static org.mule.util.ClassUtils.equal;
import static org.mule.util.ClassUtils.hash;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.routing.filter.Filter;
import org.mule.api.routing.filter.ObjectFilter;
import org.mule.config.i18n.MessageFactory;
import org.mule.routing.filters.patterns.AcceptAllWildcardPattern;
import org.mule.routing.filters.patterns.ClassNameWildcardPattern;
import org.mule.routing.filters.patterns.EnclosingStringWildcardPattern;
import org.mule.routing.filters.patterns.NoWildcardPattern;
import org.mule.routing.filters.patterns.PrefixStringWildcardPattern;
import org.mule.routing.filters.patterns.SuffixStringWildcardPattern;
import org.mule.routing.filters.patterns.WildcardPattern;
import org.mule.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>WildcardFilter</code> is used to match Strings against wildcards. It
 * performs matches with "*", i.e. "jms.events.*" would catch "jms.events.customer"
 * and "jms.events.receipts". This filter accepts a comma-separated list of patterns,
 * so more than one filter pattern can be matched for a given argument:
 * "jms.events.*, jms.actions.*" will match "jms.events.system" and "jms.actions" but
 * not "jms.queue".
 */

public class WildcardFilter implements Filter, ObjectFilter, Initialisable
{
    protected final Log logger = LogFactory.getLog(this.getClass());

    protected volatile String pattern;
    protected volatile String[] patterns;
    protected List<WildcardPattern> parsedPatterns = new ArrayList<>();
    private volatile boolean caseSensitive = true;

    public WildcardFilter()
    {
        super();
    }

    public WildcardFilter(String pattern)
    {
        this.setPattern(pattern);
    }


    @Override
    public void initialise() throws InitialisationException
    {
        if (patterns != null)
        {
            for (String pattern : patterns)
            {
                WildcardPattern parsedPattern = parsePattern(pattern);
                parsedPattern.setCaseSensitive(caseSensitive);
                parsedPatterns.add(parsedPattern);
            }
        }
    }

    private WildcardPattern parsePattern(String pattern) throws InitialisationException
    {
        if ("*".equals(pattern) || "**".equals(pattern))
        {
            return new AcceptAllWildcardPattern(null);
        }

        String candidatePattern = pattern;
        if (!isCaseSensitive())
        {
            candidatePattern = candidatePattern.toLowerCase();
        }

        int firstWildcardIndex = candidatePattern.indexOf('*');
        if (firstWildcardIndex == -1)
        {
            if (candidatePattern.endsWith("+") && candidatePattern.length() > 1)
            {
                logger.warn("wildcard-filter for payload based filtering is deprecated. Use expression" +
                            "-filter or payload-type-filter instead");
                return new ClassNameWildcardPattern(candidatePattern.substring(0, candidatePattern.length() - 1));
            }
            else
            {
                return new NoWildcardPattern(candidatePattern);
            }
        }
        else
        {
            int secondWildcardIndex = candidatePattern.indexOf('*', firstWildcardIndex + 1);
            if (firstWildcardIndex == 0 && secondWildcardIndex == candidatePattern.length()-1)
            {
                return new EnclosingStringWildcardPattern(candidatePattern.substring(1, secondWildcardIndex));
            }
            else if (firstWildcardIndex == 0 && secondWildcardIndex == -1)
            {
                return new PrefixStringWildcardPattern(candidatePattern.substring(1));
            }
            else if (firstWildcardIndex == pattern.length()-1)
            {
                return new SuffixStringWildcardPattern(pattern.substring(0, firstWildcardIndex));
            }
            else
            {
                //no other cases supported
                throw new InitialisationException(
                        MessageFactory.createStaticMessage(String.format("wildcard-filter only supports wildcards as prefix (*.log), suffix (java.util.*) or enclosing strings (*util*). Consider using a regex-filter instead for pattern: %s.", pattern)),
                        this);
            }
        }
    }

    public boolean accept(MuleMessage message)
    {
        try
        {
            return accept(message.getPayloadAsString());
        }
        catch (Exception e)
        {
            logger.warn("An exception occurred while filtering", e);
            return false;
        }
    }

    public boolean accept(Object object)
    {
        if (object == null || pattern ==null)
        {
            return false;
        }

        if (this.pattern.equals(object))
        {
            return true;
        }

        if (parsedPatterns != null)
        {
            for (WildcardPattern parsedPattern : parsedPatterns)
            {
                boolean foundMatch = parsedPattern.evaluate(object);
                if (foundMatch)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public String getPattern()
    {
        return pattern;
    }

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
        this.patterns = StringUtils.splitAndTrim(pattern, ",");
    }

    public boolean isCaseSensitive()
    {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive)
    {
        this.caseSensitive = caseSensitive;
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final WildcardFilter other = (WildcardFilter) obj;
        return equal(pattern, other.pattern)
                && equal(patterns, other.patterns)
                && caseSensitive == other.caseSensitive;
    }

    public int hashCode()
    {
        return hash(new Object[]{this.getClass(), pattern, patterns, caseSensitive});
    }
}
