/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.pgp;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicBoolean;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicLong;

/**
 * An abstract implementation of {@link TransformPolicy}.
 * 
 * Subclasses must define the behavior of the copying {@link Thread}
 */
public abstract class AbstractTransformPolicy implements TransformPolicy
{
    private StreamTransformer transformer;

    private AtomicBoolean startedCopying;

    private Thread copyingThread;

    private LazyTransformedInputStream inputStream;
    
    protected volatile boolean isClosed;
    
    private AtomicLong bytesRequested;

    public AbstractTransformPolicy(StreamTransformer transformer)
    {
        this.startedCopying = new AtomicBoolean(false);
        this.transformer = transformer;
        this.isClosed = false;
        this.bytesRequested = new AtomicLong(0);
    }
    
    /**
     * {@inheritDoc}
     */
    public void initialize(LazyTransformedInputStream lazyTransformedInputStream) {
        this.inputStream = lazyTransformedInputStream;
    }
    
    /**
     * {@inheritDoc}
     */
    public void readRequest(long length)
    {
        this.bytesRequested.addAndGet(length);
        startCopyingThread();
    }

    protected void startCopyingThread()
    {
        if (this.startedCopying.compareAndSet(false, true))
        {
            this.copyingThread = this.getCopyingThread();
            this.copyingThread.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void release()
    {
        this.isClosed = true;
        if (this.copyingThread != null)
        {
            synchronized (this.copyingThread)
            {
                this.copyingThread.notifyAll();
            }
        }
    }

    /**
     * @return an instance of the copying {@link Thread}
     */
    protected abstract Thread getCopyingThread();

    protected StreamTransformer getTransformer()
    {
        return this.transformer;
    }

    protected LazyTransformedInputStream getInputStream()
    {
        return this.inputStream;
    }
    
    protected AtomicLong getBytesRequested()
    {
        return bytesRequested;
    }
}
