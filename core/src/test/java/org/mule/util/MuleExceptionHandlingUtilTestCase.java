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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.mule.api.MuleException;
import org.mule.api.MuleRuntimeException;
import org.mule.api.transport.DispatchException;
import org.mule.config.i18n.MessageFactory;
import org.mule.tck.AbstractMuleTestCase;

import java.beans.ExceptionListener;

public class MuleExceptionHandlingUtilTestCase extends AbstractMuleTestCase
{
    public void testMuleExceptionsImplementMuleExceptionHandleStatus() throws Exception
    {
        assertTrue(MuleExceptionHandleStatus.class.isAssignableFrom(MuleException.class));
        assertTrue(MuleExceptionHandleStatus.class.isAssignableFrom(MuleRuntimeException.class));
    }

    public void testIsExceptionHandled_FalseForNonMuleExceptionHandleStatus() throws Exception
    {
        Exception e = new Exception();
        assertFalse(MuleExceptionHandlingUtil.isExceptionHandled(e));
        assertFalse(ExceptionUtils.containsType(e, MuleExceptionHandleStatus.class));
    }

    public void testIsExceptionHandled_simplestCase() throws Exception
    {
        MuleException e = new DispatchException(null, null);
        MuleExceptionHandleStatus eHandleStatus = e;
        eHandleStatus.setExceptionAlreadyHandled(false);
        assertFalse(MuleExceptionHandlingUtil.isExceptionHandled(e));
        eHandleStatus.setExceptionAlreadyHandled(true);
        assertTrue(MuleExceptionHandlingUtil.isExceptionHandled(e));
    }

    public void testIsExceptionHandled_withASourroundingMuleExceptionHandleStatusException() throws Exception
    {
        MuleException cause = new DispatchException(null, null);
        MuleException wrapper = new DispatchException(null, null, cause);
        MuleExceptionHandleStatus eHandleStatusForCause = cause;
        MuleExceptionHandleStatus eHandleStatusForWrapper = wrapper;

        eHandleStatusForCause.setExceptionAlreadyHandled(false);
        eHandleStatusForWrapper.setExceptionAlreadyHandled(false);
        assertFalse(MuleExceptionHandlingUtil.isExceptionHandled(wrapper));

        eHandleStatusForCause.setExceptionAlreadyHandled(false);
        eHandleStatusForWrapper.setExceptionAlreadyHandled(true);
        assertTrue(MuleExceptionHandlingUtil.isExceptionHandled(wrapper));

        eHandleStatusForCause.setExceptionAlreadyHandled(true);
        eHandleStatusForWrapper.setExceptionAlreadyHandled(false);
        assertTrue(MuleExceptionHandlingUtil.isExceptionHandled(wrapper));

        eHandleStatusForCause.setExceptionAlreadyHandled(true);
        eHandleStatusForWrapper.setExceptionAlreadyHandled(true);
        assertTrue(MuleExceptionHandlingUtil.isExceptionHandled(wrapper));
    }

    public void testSetExceptionHandled_simplestCase() throws Exception
    {
        MuleException e = new DispatchException(null, null);

        assertFalse(e.isExceptionAlreadyHandled());
        MuleExceptionHandlingUtil.markExceptionAsHandled(e);
        assertTrue(e.isExceptionAlreadyHandled());
        assertTrue(MuleExceptionHandlingUtil.isExceptionHandled(e));
    }

    public void testSetExceptionHandled_withASourroundingMuleExceptionHandleStatusException()
        throws Exception
    {
        MuleException cause = new DispatchException(null, null);
        MuleException wrapper = new DispatchException(null, null, cause);

        assertFalse(cause.isExceptionAlreadyHandled());
        MuleExceptionHandlingUtil.markExceptionAsHandled(wrapper);
        assertTrue(cause.isExceptionAlreadyHandled());
        assertTrue(MuleExceptionHandlingUtil.isExceptionHandled(cause));
        assertTrue(MuleExceptionHandlingUtil.isExceptionHandled(wrapper));
    }

    public void testSetExceptionHandled_nonMuleExceptionHandleStatusExceptionDontGetMarked() throws Exception
    {
        Exception cause = new RuntimeException();

        assertFalse(MuleExceptionHandlingUtil.isExceptionHandled(cause));
        MuleExceptionHandlingUtil.markExceptionAsHandled(cause);
        assertFalse(MuleExceptionHandlingUtil.isExceptionHandled(cause));
    }

    public void testHandledExceptionIfNeeded_withRegularException() throws Exception
    {
        Exception e = new Exception();

        ExceptionListener exceptionListener = mock(ExceptionListener.class);
        assertTrue(MuleExceptionHandlingUtil.handledExceptionIfNeeded(exceptionListener, e));
        assertFalse(MuleExceptionHandlingUtil.isExceptionHandled(e));
        assertTrue(MuleExceptionHandlingUtil.handledExceptionIfNeeded(exceptionListener, e));
        
        verify(exceptionListener, times(2)).exceptionThrown(e);
    }

    public void testHandledExceptionIfNeeded_withAMuleExceptionHandleStatusException() throws Exception
    {
        Exception e = new MuleRuntimeException(MessageFactory.createStaticMessage("Dummy"));

        ExceptionListener exceptionListener = mock(ExceptionListener.class);
        assertTrue(MuleExceptionHandlingUtil.handledExceptionIfNeeded(exceptionListener, e));
        assertTrue(MuleExceptionHandlingUtil.isExceptionHandled(e));
        assertFalse(MuleExceptionHandlingUtil.handledExceptionIfNeeded(exceptionListener, e));

        verify(exceptionListener, times(1)).exceptionThrown(e);
    }

    public void testHandledExceptionIfNeeded_withNullExceptionListener() throws Exception
    {
        Exception e = new MuleRuntimeException(MessageFactory.createStaticMessage("Dummy"));

        ExceptionListener exceptionListener = null;
        assertFalse(MuleExceptionHandlingUtil.handledExceptionIfNeeded(exceptionListener, e));
        assertFalse(MuleExceptionHandlingUtil.isExceptionHandled(e));
        assertFalse(MuleExceptionHandlingUtil.handledExceptionIfNeeded(exceptionListener, e));
    }

    public void testHandledExceptionIfNeeded_exceptionListenerThrowsException() throws Exception
    {
        Exception e = new MuleRuntimeException(MessageFactory.createStaticMessage("Dummy"));

        ExceptionListener exceptionListener = mock(ExceptionListener.class);
        IllegalStateException toBeThrown = new IllegalStateException("dummy exception");
        doThrow(toBeThrown).when(exceptionListener).exceptionThrown(e);
        
        try
        {
            MuleExceptionHandlingUtil.handledExceptionIfNeeded(exceptionListener, e);
            fail("It should have thrown exception");
        }
        catch (IllegalStateException exception)
        {
            assertSame(toBeThrown, exception);
        }
        assertTrue(MuleExceptionHandlingUtil.isExceptionHandled(e));

        verify(exceptionListener, times(1)).exceptionThrown(e);
    }
}
