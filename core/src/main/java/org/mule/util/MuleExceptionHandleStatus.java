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

/**
 * This interface must be implemented by {@link Exception Exceptions} that want to be
 * considered when being sent to the methods of {@link MuleExceptionHandlingUtil}.
 */
public interface MuleExceptionHandleStatus
{
    boolean isExceptionAlreadyHandled();

    void setExceptionAlreadyHandled(boolean flag);

}


