/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import javax.jms.Message;

import org.junit.Test;

public class JmsPublishTestCase extends AbstractJmsTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "jms-publish-test-case.xml";
    }

    @Test
    public void test() throws Exception
    {
        runFlow("testFlow");
        Message message = getJmsTemplate().receive("myQueue");
        assertThat(message, notNullValue());
    }
}
