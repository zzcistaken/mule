/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.endpoints;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import org.mule.api.annotations.Transformer;
import org.mule.api.transformer.TransformerException;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.transformer.AbstractTransformer;
import org.mule.util.concurrent.Latch;

public class OneWayEndpointTestCase extends FunctionalTestCase
{
    private static Latch latch;
    private static boolean requestTransformersRun;
    private static boolean responseTransformersRun;

    @Override
    protected String getConfigResources()
    {
        return "one-way-endpoint-test.xml";
    }

    public void testBasic() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        latch = new Latch();
        client.dispatch("vm://input", "Hello", null);
        latch.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(requestTransformersRun);
        assertFalse(responseTransformersRun);
    }

    public static class NoopRequestTransformer extends AbstractTransformer
    {
        @Override
        protected Object doTransform(Object src, String enc) throws TransformerException
        {
            requestTransformersRun = true;
            return src;
        }
    }

    public static class NoopResponseTransformer extends AbstractTransformer
    {
        @Override
        protected Object doTransform(Object src, String enc) throws TransformerException
        {
            responseTransformersRun = true;
            latch.countDown();
            return src;
        }
    }

    public static class VoidMethodComponent
    {
        public void method(String str)
        {
            System.out.println("Received: " + str);
        }
    }
}
