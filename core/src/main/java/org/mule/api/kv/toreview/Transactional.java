/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.kv.toreview;

/**
 *
 */
public interface Transactional
{

    /**
     * Opens a transaction and set it as the current open transaction.
     * @return The new created transaction
     */
    StoreTransaction openTransaction();

    /**
     * @return true if there is an transaction open and set as current
     */
    boolean isTransactionOpen();

    /**
     * @return The current open transaction if one exists, null if not
     */
    StoreTransaction getCurrentTransaction();

    /**
     * Sets the passed transaction as the current one.
     * This method is usefull when a store is used in a multi threaded environment.
     * @param storeTransaction
     */
    void setCurrentTransaction(StoreTransaction storeTransaction);

    /**
     * Commits the current open transaction if one exists.
     */
    void commitTransaction();

    /**
     * Rollbacks the current open transaction if one exists.
     */
    void rollbackTransaction();
}
