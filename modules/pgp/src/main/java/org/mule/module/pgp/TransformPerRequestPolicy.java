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

import java.io.PipedOutputStream;

import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A {@link TransformPolicy} that copies only the requested transformed bytes 
 * into the {@link PipedOutputStream}.
 */
public class TransformPerRequestPolicy extends AbstractTransformPolicy
{

    private static final Log logger = LogFactory.getLog(TransformPerRequestPolicy.class);
    
    protected Semaphore writeSemaphore;
    
    public TransformPerRequestPolicy(StreamTransformer transformer)
    {
        super(transformer);
        this.writeSemaphore = new Semaphore(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readRequest(long length)
    {
        super.readRequest(length);
        this.writeSemaphore.release();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void release()
    {
        this.writeSemaphore.release();
        super.release();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Thread getCopyingThread()
    {
        return new PerRequestWork();
    }

    private class PerRequestWork extends Thread
    {
        public synchronized void run()
        {
            try
            {
                getTransformer().initialize(getInputStream().getOut());
                
                boolean finishWriting = false;
                while (!finishWriting && !isClosed)
                {
                    writeSemaphore.acquire();
                    finishWriting = getTransformer().write(getInputStream().getOut(), getBytesRequested());
                }
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(), e);
            }
            finally
            {
                IOUtils.closeQuietly(getInputStream().getOut());
                //keep the thread alive so that we don't break the pipe
                while (!isClosed)
                {
                    try
                    {
                        this.wait();
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
            }
        }
    }
}


