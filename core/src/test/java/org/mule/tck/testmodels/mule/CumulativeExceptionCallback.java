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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @ThreadSafe
 */
public class CumulativeExceptionCallback implements ExceptionCallback
{
    /**
     * logger used by this class
     */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * This is the lock that protect both the storage of {@link #callback} and
     * modifications of {@link #unhandled}.
     */
    private Object callbackLock = new Object();

    // @GuardedBy("callbackLock")
    private ExceptionCallback callback;
    // @GuardedBy("callbackLock")
    private List<Throwable> unhandled = new LinkedList<Throwable>();

    /**
     */
    public void onException(Throwable e)
    {
        ExceptionCallback callback = null;
        synchronized (callbackLock)
        {
            if (this.callback != null)
            {
                callback = this.callback;
            }
            else
            {
                unhandled.add(e);
            }
        }
        // It is important that the call to the callback is done outside
        // synchronization since we don't control that code and
        // we could have liveness problems.
        if (callback != null)
        {
            logger.info("Exception caught on TestExceptionStrategy and was sent to callback.", e);
            callback.onException(e);
        }
        else
        {
            logger.info("Exception caught on TestExceptionStrategy but there was no callback set.", e);
        }

    }

    public void setExceptionCallback(ExceptionCallback exceptionCallback)
    {
        synchronized (callbackLock)
        {
            this.callback = exceptionCallback;
        }
        processUnhandled();
    }

    protected void processUnhandled()
    {
        List<Throwable> unhandledCopies = null;
        ExceptionCallback callback = null;
        synchronized (callbackLock)
        {
            if (this.callback != null)
            {
                callback = this.callback;
                unhandledCopies = new ArrayList<Throwable>(unhandled);
                unhandled.clear();
            }
        }
        // It is important that the call to the callback is done outside
        // synchronization since we don't control that code and
        // we could have liveness problems.
        if (callback != null && unhandledCopies != null)
        {
            for (Throwable exception : unhandledCopies)
            {
                logger.info("Handling exception after setting the callback.", exception);
                callback.onException(exception);
            }
        }
    }
}


