/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.execution;

/**
 * Provides information about the current execution of an {@link org.mule.api.execution.ExecutionTemplate}
 */
public class ExecutionContext
{

    private boolean transactionStarted;

    /**
     * @return true if within the current execution template there was a transaction started
     */
    public boolean isTransactionStarted()
    {
        return transactionStarted;
    }

    /**
     * this method must be called whenever a transaction has been created in the execution context
     */
    public void transactionStarted()
    {
        this.transactionStarted = true;
    }
}
