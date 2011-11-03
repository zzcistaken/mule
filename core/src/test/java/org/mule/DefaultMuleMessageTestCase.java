/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule;

import org.mule.api.MuleException;
import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.transformer.AbstractTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultMuleMessageTestCase extends AbstractMuleTestCase
{
    private static final String TRANSFORMER1 = "Transformer 1";
    private static final String TRANSFORMER2 = "Transformer 2";
    private static final String TRANSFORMER3 = "Transformer 3";


    // I can't reproduce the heavy load issue that generated the same hashcode for two different
    // transformers, but this test ensures that the appliedTransformers check is behaving as expected
    public void testApplyTransformersWithGenerateTransformerListHashCode() throws MuleException
    {
        DefaultMuleMessage defaultMuleMessage = new DefaultMuleMessage(TEST_MESSAGE, (Map) null);
        List<Transformer> transformersList1 = new ArrayList<Transformer>();
        List<Transformer> transformersList2 = new ArrayList<Transformer>();
        List<Transformer> transformersList3 = new ArrayList<Transformer>();

        TestTransformer1 transformer1 = new TestTransformer1();
        TestTransformer2 transformer2 = new TestTransformer2();
        TestTransformer3 transformer3 = new TestTransformer3();

        transformersList1.add(transformer1);
        transformersList1.add(transformer2);

        transformersList2.add(transformer1);
        transformersList2.add(transformer2);

        transformersList3.add(transformer3);
        transformersList3.add(transformer1);

        defaultMuleMessage.applyTransformers(transformersList1);
        assertNotNull(defaultMuleMessage.getPayload());
        String payload = TEST_MESSAGE + TRANSFORMER1 + TRANSFORMER2;
        assertEquals(payload, defaultMuleMessage.getPayload());
        defaultMuleMessage.applyTransformers(transformersList2);
        assertEquals(payload, defaultMuleMessage.getPayload());
        defaultMuleMessage.applyTransformers(transformersList3);
        assertEquals(payload + TRANSFORMER3 + TRANSFORMER1, defaultMuleMessage.getPayload());
    }


    public class TestTransformer1 extends AbstractTransformer
    {
        protected Object doTransform(Object obj, String encoding) throws TransformerException
        {
            if(obj instanceof String)
            {
                String text = (String)obj;
                return text.concat(TRANSFORMER1);
            }
            return obj;
        }
    }

    public class TestTransformer2 extends AbstractTransformer
    {
        protected Object doTransform(Object obj, String encoding) throws TransformerException
        {
            if(obj instanceof String)
            {
                String text = (String)obj;
                return text.concat(TRANSFORMER2);
            }
            return obj;
        }
    }

    public class TestTransformer3 extends AbstractTransformer
    {
        protected Object doTransform(Object obj, String encoding) throws TransformerException
        {
            if(obj instanceof String)
            {
                String text = (String)obj;
                return text.concat(TRANSFORMER3);
            }
            return obj;
        }
    }


}
