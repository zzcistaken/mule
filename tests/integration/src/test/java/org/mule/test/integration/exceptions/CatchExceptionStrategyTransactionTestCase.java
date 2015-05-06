/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.exceptions;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;
import org.mule.api.exception.SystemExceptionHandler;
import org.mule.api.transaction.Transaction;
import org.mule.tck.AbstractServiceAndFlowTestCase;
import org.mule.tck.functional.EventCallback;
import org.mule.tck.listener.ExceptionListener;
import org.mule.tck.listener.SystemExceptionListener;
import org.mule.transaction.TransactionCoordination;

import java.util.Arrays;
import java.util.Collection;

import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CatchExceptionStrategyTransactionTestCase extends AbstractServiceAndFlowTestCase
{

    public static final int TIMEOUT = 5000;
    public static final String MESSAGE = "any message";
    public static final String SINGLE_TRANSACTION_BEHAVIOR_FLOW = "singleTransactionBehavior";
    public static final String XA_TRANSACTION_BEHAVIOR_FLOW = "xaTransactionBehavior";
    public static final String XA_TRANSACTION_COMMIT_FAILS_FLOW = "xaTransactionCommitFails";
    private SystemExceptionHandler mockSystemExceptionHandler = mock(SystemExceptionHandler.class);
    private Transaction mockTransaction = mock(Transaction.class);;

    public CatchExceptionStrategyTransactionTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
                {ConfigVariant.FLOW, "org/mule/test/integration/exceptions/catch-exception-strategy-transaction-flow.xml"}});
    }

    @Test
    public void testSingleTransactionIsCommittedOnFailure() throws Exception
    {
        LocalMuleClient client = muleContext.getClient();
        ExceptionListener exceptionListener = new ExceptionListener(muleContext);
        exceptionListener.setTimeoutInMillis(TIMEOUT);
        client.dispatch("jms://in1?connector=activeMq", MESSAGE, null);
        exceptionListener.waitUntilAllNotificationsAreReceived();
        stopFlowConstruct(SINGLE_TRANSACTION_BEHAVIOR_FLOW);
        MuleMessage request = client.request("jms://in?connector=activeMq", TIMEOUT);
        assertThat(request, IsNull.<Object>nullValue());
    }

    @Test
    public void testSingleTransactionIsCommittedOnFailureButCommitFails() throws Exception
    {
        muleContext.setExceptionListener(mockSystemExceptionHandler);
        getFunctionalTestComponent(SINGLE_TRANSACTION_BEHAVIOR_FLOW).setEventCallback(replaceTransactionWithMock());
        LocalMuleClient client = muleContext.getClient();
        ExceptionListener exceptionListener = new ExceptionListener(muleContext);
        exceptionListener.setTimeoutInMillis(TIMEOUT);
        SystemExceptionListener systemExceptionListener = new SystemExceptionListener(muleContext).setTimeoutInMillis(TIMEOUT);
        client.dispatch("jms://in1?connector=activeMq", MESSAGE, null);
        exceptionListener.waitUntilAllNotificationsAreReceived();
        stopFlowConstruct(SINGLE_TRANSACTION_BEHAVIOR_FLOW);
        systemExceptionListener.waitUntilAllNotificationsAreReceived();
        MuleMessage request = client.request("jms://in?connector=activeMq", TIMEOUT);
        assertThat(request, IsNull.<Object>nullValue());
    }

    @Test
    public void testXaTransactionIsCommittedOnFailure() throws Exception
    {
        LocalMuleClient client = muleContext.getClient();
        ExceptionListener exceptionListener = new ExceptionListener(muleContext);
        exceptionListener.setTimeoutInMillis(TIMEOUT);
        client.dispatch("jms://in2?connector=activeMq", MESSAGE, null);
        exceptionListener.waitUntilAllNotificationsAreReceived();
        stopFlowConstruct(XA_TRANSACTION_BEHAVIOR_FLOW);
        MuleMessage outMessage = client.request("jms://out2?connector=activeMq", TIMEOUT);
        assertThat(outMessage,IsNull.<Object>notNullValue());
        assertThat(outMessage.getPayloadAsString(), is(MESSAGE));
        MuleMessage inMessage = client.request("jms://in2?connector=activeMq", TIMEOUT);
        assertThat(inMessage,IsNull.<Object>nullValue());
        MuleMessage inVmMessage = client.request("vm://vmIn2",TIMEOUT);
        assertThat(inVmMessage, IsNull.<Object>notNullValue());
        assertThat(inVmMessage.getPayloadAsString(), is(MESSAGE));
    }

    @Test
    public void testXaTransactionIsCommittedOnFailureButCommitFails() throws Exception
    {
        muleContext.setExceptionListener(mockSystemExceptionHandler);
        getFunctionalTestComponent(XA_TRANSACTION_COMMIT_FAILS_FLOW).setEventCallback(replaceTransactionWithMock());
        LocalMuleClient client = muleContext.getClient();
        ExceptionListener exceptionListener = new ExceptionListener(muleContext);
        exceptionListener.setTimeoutInMillis(TIMEOUT);
        SystemExceptionListener systemExceptionListener = new SystemExceptionListener(muleContext).setTimeoutInMillis(TIMEOUT);
        client.dispatch("jms://in3?connector=activeMq", MESSAGE, null);
        exceptionListener.waitUntilAllNotificationsAreReceived();
        stopFlowConstruct(XA_TRANSACTION_COMMIT_FAILS_FLOW);
        systemExceptionListener.waitUntilAllNotificationsAreReceived();
        MuleMessage outMessage = client.request("jms://out2?connector=activeMq", TIMEOUT);
        assertThat(outMessage,IsNull.<Object>nullValue());
        MuleMessage inMessage = client.request("jms://in2?connector=activeMq", TIMEOUT);
        assertThat(inMessage,IsNull.<Object>nullValue());
        MuleMessage inVmMessage = client.request("vm://in2",TIMEOUT);
        assertThat(inVmMessage, IsNull.<Object>nullValue());
    }

    private EventCallback replaceTransactionWithMock()
    {
        return new EventCallback()
        {
            @Override
            public void eventReceived(MuleEventContext context, Object component) throws Exception
            {
                context.getCurrentTransaction().rollback();
                TransactionCoordination.getInstance().bindTransaction(mockTransaction);
                when(mockTransaction.supports(anyObject(), anyObject())).thenReturn(true);
                doAnswer(new Answer()
                {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable
                    {
                        TransactionCoordination.getInstance().unbindTransaction(mockTransaction);
                        throw new RuntimeException();
                    }
                }).when(mockTransaction).commit();
            }
        };
    }

}
