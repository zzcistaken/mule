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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A {@link TransformPolicy} that copies the transformed bytes continuously into the {@link PipedOutputStream}
 * without taking into account about how many bytes the object has requested.
 */
public class TransformContinuouslyPolicy extends AbstractTransformPolicy
{

    private static final Log logger = LogFactory.getLog(TransformContinuouslyPolicy.class);
    
    public static final long DEFAULT_CHUNK_SIZE = 1 << 24;
    
    private long chunkSize;

    public TransformContinuouslyPolicy(StreamTransformer transformer)
    {
        this(transformer, DEFAULT_CHUNK_SIZE);
    }

    public TransformContinuouslyPolicy(StreamTransformer transformer, long chunkSize)
    {
        super(transformer);
        this.chunkSize = chunkSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readRequest(long length)
    {
        //avoid calling super so that we don't add more bytes. 
        //The ContinuousWork will add the requested bytes as necessary
        //only start copying thread
        startCopyingThread();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Thread getCopyingThread()
    {
        return new ContinuousWork();
    }

    private class ContinuousWork extends Thread
    {
        public synchronized void run()
        {
            try
            {
                getTransformer().initialize(getInputStream().getOut());
                
                boolean finishWriting = false;
                while (!finishWriting)
                {
                    getBytesRequested().addAndGet(chunkSize);
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


