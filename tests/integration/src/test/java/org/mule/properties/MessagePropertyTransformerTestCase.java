/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.mule.api.MuleMessage;
import org.mule.tck.junit4.FunctionalTestCase;

import org.hamcrest.core.Is;
import org.junit.Test;

public class MessagePropertyTransformerTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return null;
    }

    @Test
    public void testTemplate() throws Exception
    {
        MuleMessage response = muleContext.getClient().send("vm://in", "message from client", null, 5000);
        assertThat(response.getPayloadAsString(),
                   is("Hello john malakalis. You executed service exampleTemplateUsage"));
        assertThat(response.<Integer>getOutboundProperty("response.status"), Is.is(0));
    }
}
