/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.util;

import java.beans.ExceptionListener;
import java.util.List;

/**
 * This utility class should be used whenever you want to call
 * {@link ExceptionListener#exceptionThrown(Exception)} and you want to make sure
 * that you don't call that method twice for the same exception (meaning, it will be
 * reported to only one listener).
 */
public class MuleExceptionHandlingUtil
{

    /**
     * Determines if the exception was already handled. And exception is considered
     * "handled" if it or any of its causes implements
     * {@link MuleExceptionHandleStatus} and its
     * {@link MuleExceptionHandleStatus#isExceptionAlreadyHandled()} returns true.
     * This means that if you wrap an already handled exception, it will still be
     * considered handed by this method.
     * 
     * @param e
     * @return
     */
    public static boolean isExceptionHandled(Exception e)
    {

        @SuppressWarnings("unchecked")
        List<Throwable> throwableList = ExceptionUtils.getThrowableList(e);
        for (Throwable throwable : throwableList)
        {
            if (throwable instanceof MuleExceptionHandleStatus)
            {
                MuleExceptionHandleStatus handleStatus = (MuleExceptionHandleStatus) throwable;
                if (handleStatus.isExceptionAlreadyHandled())
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Marks the exception as handled by calling the
     * {@link MuleExceptionHandleStatus#setExceptionAlreadyHandled(boolean)} on the
     * exception that implements {@link MuleExceptionHandleStatus} and it is closes
     * to the root cause. If no exception in all the causes of the exception
     * implement the {@link MuleExceptionHandleStatus}, then this method does
     * nothing.
     * 
     * @param e
     */
    public static <E extends Exception> E markExceptionAsHandled(E e)
    {
        MuleExceptionHandleStatus handleStatus = getMuleExceptionHandleStatus(e);
        if (handleStatus != null)
        {
            handleStatus.setExceptionAlreadyHandled(true);
        }
        return e;
    }

    private static MuleExceptionHandleStatus getMuleExceptionHandleStatus(Exception e)
    {
        return (MuleExceptionHandleStatus) ExceptionUtils.getDeepestOccurenceOfType(
            e, MuleExceptionHandleStatus.class);
    }

    /**
     * This method is just a helper that will call
     * {@link ExceptionListener#exceptionThrown(Exception)} if the supplied
     * <code>exceptionListener</code> is not null and the exception was not already
     * handled according to {@link #isExceptionHandled(Exception)}.
     * 
     * @param exceptionListener
     * @param e
     * @return
     */
    public static boolean handledExceptionIfNeeded(ExceptionListener exceptionListener, Exception e)
    {
        boolean needToHandleException = exceptionListener != null && !isExceptionHandled(e);
        if (needToHandleException)
        {
            try
            {
                exceptionListener.exceptionThrown(e);
            }
            finally
            {
                markExceptionAsHandled(e);
            }
        }
        return needToHandleException;
    }

}
