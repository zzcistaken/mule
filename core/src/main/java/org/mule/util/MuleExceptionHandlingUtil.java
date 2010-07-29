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

/**
 * This utility class should be used whenever you want to call
 * {@link ExceptionListener#exceptionThrown(Exception)} and you want to make sure
 * that you don't call that method twice for the same exception (meaning, it will be
 * reported to only one listener).
 */
public class MuleExceptionHandlingUtil
{
    final static ThreadLocal<Throwable> lastHandledExceptionThreadLocal = new ThreadLocal<Throwable>();

    /**
     * Determines if the exception was already handled. And exception is considered
     * "handled" if it (or any of its wrapper or wrapped exceptions) was sent to
     * {@link #markExceptionAsHandled(Exception)} or
     * {@link #handledExceptionIfNeeded(ExceptionListener, Exception)}. This means
     * that if you wrap an already handled exception, it will still be considered
     * handed by this method.
     * 
     * @param currentException
     * @return
     */
    public static boolean isExceptionHandled(Exception currentException)
    {
        Throwable lastHandledException = lastHandledExceptionThreadLocal.get();
        if (lastHandledException == null)
        {
            return false;
        }
        else
        {
            Throwable[] lastHandledExceptionCauses = ExceptionUtils.getThrowables(lastHandledException);
            Throwable[] currentExceptionCauses = ExceptionUtils.getThrowables(currentException);
            for (int i = lastHandledExceptionCauses.length - 1; i >= 0; i--)
            {
                for (int j = currentExceptionCauses.length - 1; j >= 0; j--)
                {
                    if (lastHandledExceptionCauses[i] == currentExceptionCauses[j])
                    {
                        return true;
                    }
                }
            }
            return false;

        }
    }

    /**
     * Marks the exception as handled.
     * 
     * @param e
     */
    public static <E extends Exception> E markExceptionAsHandled(E e)
    {
        lastHandledExceptionThreadLocal.set(e);
        return e;
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
