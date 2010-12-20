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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicLong;

public class LazyTransformedInputStreamTestCase extends TestCase
{

    public void testTransformPerRequestPolicy() throws Exception
    {
        String message = "abcdefghij";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(message.getBytes());
        AddOneStreamTransformer simpleTransformer = new AddOneStreamTransformer(inputStream);
        LazyTransformedInputStream transformedInputStream = new LazyTransformedInputStream(
            new TransformPerRequestPolicy(simpleTransformer));

        for (int i = 0; i < message.length(); i++)
        {
            int read = transformedInputStream.read();
            assertEquals(message.charAt(i) + 1, read);
            // only one byte more should be consumed at this point
            assertEquals(i + 1, simpleTransformer.bytesRead);
            Thread.sleep(50);
            assertEquals(i + 1, simpleTransformer.bytesRead);
        }

        transformedInputStream.close();
    }

    public void testTransformContinuouslyPolicy() throws Exception
    {
        String message = "abcdefghij";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(message.getBytes());
        AddOneStreamTransformer simpleTransformer = new AddOneStreamTransformer(inputStream);
        LazyTransformedInputStream transformedInputStream = new LazyTransformedInputStream(
            new TransformContinuouslyPolicy(simpleTransformer));

        int i = 0;
        int read = transformedInputStream.read();
        Thread.sleep(500);
        // all input stream should be consumed at this point
        assertEquals(message.length(), simpleTransformer.bytesRead);
        do
        {
            assertEquals(message.charAt(i) + 1, read);
            read = transformedInputStream.read();
            i++;
        }
        while (i < message.length());

        transformedInputStream.close();
    }

    private class AddOneStreamTransformer implements StreamTransformer
    {

        private InputStream inputStream;

        private int bytesRead;
        
        private boolean finished;

        public AddOneStreamTransformer(InputStream inputStream)
        {
            this.inputStream = inputStream;
            this.bytesRead = 0;
            this.finished = false;
        }

        public boolean write(OutputStream out, AtomicLong bytesRequested) throws Exception
        {
            
            while (!this.finished && this.bytesRead + 1 <= bytesRequested.get())
            {
                int byteRead = this.inputStream.read();
                if (byteRead == -1)
                {
                    finished = true;
                    out.write(-1);
                }
                else
                {
                    out.write(byteRead + 1);
                    this.bytesRead++;
                }
            }
            return finished;
        }

        public void initialize(OutputStream out) throws Exception
        {
        }
    }
}
