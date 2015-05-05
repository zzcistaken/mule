/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.execution;

import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.api.execution.ExecutionCallback;
import org.mule.transaction.TransactionCoordination;

/**
 * Commits any pending transaction.
 *
 * This interceptor must be executed before the error handling interceptor so if
 * there is any failure doing commit, the error handler gets executed.
 */
class CommitTransactionInterceptor implements ExecutionInterceptor<MuleEvent>
{

    private final ExecutionInterceptor<MuleEvent> nextInterceptor;

    public CommitTransactionInterceptor(ExecutionInterceptor<MuleEvent> nextInterceptor)
    {
        this.nextInterceptor = nextInterceptor;
    }

    @Override
    public MuleEvent execute(ExecutionCallback<MuleEvent> callback) throws Exception
    {
        MuleEvent result = nextInterceptor.execute(callback);
        try
        {
            if (TransactionCoordination.getInstance().getTransaction() != null)
            {
                TransactionCoordination.getInstance().resolveTransaction();
            }
        }
        catch (Exception e)
        {
            throw new MessagingException(result, e);
        }
        return result;
    }
}
