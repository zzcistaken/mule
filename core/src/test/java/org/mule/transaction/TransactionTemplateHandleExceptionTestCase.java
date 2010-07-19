/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transaction;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.mule.api.MuleContext;
import org.mule.api.transaction.Transaction;
import org.mule.api.transaction.TransactionConfig;
import org.mule.api.transaction.TransactionException;
import org.mule.config.i18n.MessageFactory;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.transport.ConnectException;

import java.beans.ExceptionListener;

public class TransactionTemplateHandleExceptionTestCase extends AbstractMuleTestCase
{
    private TransactionTemplate transactionTemplate;
    private ExceptionListener listener;
    private TransactionConfig config;
    private MuleContext context;

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        config = mock(TransactionConfig.class);
        listener = mock(ExceptionListener.class);
        context = mock(MuleContext.class);

        transactionTemplate = new TransactionTemplate(config, listener, context);
    }

    public void testHandleException_withTransactionSuspendedTransactionAndExceptionLister()
        throws TransactionException, Exception
    {
        Exception e = new Exception();
        Transaction tx = mock(Transaction.class);
        Transaction suspendedXATx = mock(Transaction.class);

        Object objectReturned = transactionTemplate.handleException(e, tx, suspendedXATx);

        assertNull(objectReturned);
        verify(listener, times(1)).exceptionThrown(e);
        verify(tx, times(0)).setRollbackOnly();
        verify(suspendedXATx, times(1)).resume();
    }

    public void testHandleException_withTransactionAndExceptionListerWithoutSuspendedTransaction()
        throws TransactionException, Exception
    {
        Exception e = new Exception();
        Transaction tx = mock(Transaction.class);
        Transaction suspendedXATx = null;

        Object objectReturned = transactionTemplate.handleException(e, tx, suspendedXATx);

        assertNull(objectReturned);
        verify(listener, times(1)).exceptionThrown(e);
        verify(tx, times(0)).setRollbackOnly();
    }

    public void testHandleException_withTransactionWithoutExceptionListerAndSuspendedTransaction()
        throws TransactionException, Exception
    {
        Exception e = new Exception();
        Transaction tx = mock(Transaction.class);
        Transaction suspendedXATx = null;

        TransactionTemplate transactionTemplate = new TransactionTemplate(config, null, context);
        try
        {
            transactionTemplate.handleException(e, tx, suspendedXATx);
            fail("Should have thrown an exception");
        }
        catch (Exception thrownException)
        {
            assertSame(e, thrownException);
        }
        verify(listener, times(0)).exceptionThrown(e);
        verify(tx, times(1)).setRollbackOnly();
    }

    public void testHandleConnectException_withTransactionWithoutExceptionListerAndSuspendedTransaction()
        throws TransactionException, Exception
    {
        Exception connectException = new ConnectException(MessageFactory.createStaticMessage("dummy"), null);
        Transaction tx = mock(Transaction.class);
        Transaction suspendedXATx = null;

        TransactionTemplate transactionTemplate = new TransactionTemplate(config, null, context);
        try
        {
            transactionTemplate.handleException(connectException, tx, suspendedXATx);
            fail("Should have thrown an exception");
        }
        catch (Exception thrownException)
        {
            assertSame(connectException, thrownException);
        }
        verify(listener, times(0)).exceptionThrown(connectException);
        verify(tx, times(1)).setRollbackOnly();
    }

    public void testHandleWrappedConnectException_withTransactionWithoutExceptionListerAndSuspendedTransaction()
        throws TransactionException, Exception
    {
        ConnectException connectException = new ConnectException(MessageFactory.createStaticMessage("dummy"),
            null);
        Exception e = new Exception(connectException);
        Transaction tx = mock(Transaction.class);
        Transaction suspendedXATx = null;

        TransactionTemplate transactionTemplate = new TransactionTemplate(config, null, context);
        try
        {
            transactionTemplate.handleException(e, tx, suspendedXATx);
            fail("Should have thrown an exception");
        }
        catch (Exception thrownException)
        {
            assertSame(connectException, thrownException);
        }
        verify(listener, times(0)).exceptionThrown(e);
        verify(tx, times(1)).setRollbackOnly();
    }

    public void testHandleConnectException_withTransactionAndExceptionListerWithoutSuspendedTransaction()
        throws TransactionException, Exception
    {
        Exception connectException = new ConnectException(MessageFactory.createStaticMessage("dummy"), null);
        Transaction tx = mock(Transaction.class);
        Transaction suspendedXATx = null;

        try
        {
            transactionTemplate.handleException(connectException, tx, suspendedXATx);
            fail("Should have thrown an exception");
        }
        catch (Exception thrownException)
        {
            assertSame(connectException, thrownException);
        }
        verify(listener, times(1)).exceptionThrown(connectException);
        verify(tx, times(0)).setRollbackOnly();
    }

    public void testHandleWrappedConnectException_withTransactionAndExceptionListerWithoutSuspendedTransaction()
        throws TransactionException, Exception
    {
        ConnectException connectException = new ConnectException(MessageFactory.createStaticMessage("dummy"),
            null);
        Exception e = new Exception(connectException);
        Transaction tx = mock(Transaction.class);
        Transaction suspendedXATx = null;

        try
        {
            transactionTemplate.handleException(e, tx, suspendedXATx);
            fail("Should have thrown an exception");
        }
        catch (Exception thrownException)
        {
            assertSame(connectException, thrownException);
        }
        verify(listener, times(1)).exceptionThrown(e);
        verify(tx, times(0)).setRollbackOnly();
    }
}
