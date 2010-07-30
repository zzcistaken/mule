/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tck.testmodels.mule;

import org.mule.DefaultExceptionStrategy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>TestExceptionStrategy</code> is used by the Mule test cases as a direct
 * replacement of the {@link org.mule.DefaultExceptionStrategy}. This is used to test
 * that overriding the default Exception strategy works.
 */
public class TestExceptionStrategy extends DefaultExceptionStrategy
{
    /**
     * logger used by this class
     */
    protected final Log logger = LogFactory.getLog(getClass());

    private volatile String testProperty;

    private final CumulativeExceptionCallback exceptionCallback = new CumulativeExceptionCallback();

    public String getTestProperty()
    {
        return testProperty;
    }

    public void setTestProperty(String testProperty)
    {
        this.testProperty = testProperty;
    }

    @Override
    public void exceptionThrown(Exception e)
    {
        exceptionCallback.onException(e);
    }

    public void setExceptionCallback(ExceptionCallback exceptionCallback)
    {
        this.exceptionCallback.setExceptionCallback(exceptionCallback);
    }

}
