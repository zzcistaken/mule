/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.hamcrest.core.Is;
import org.junit.Test;

public class JmsPublishTestCase extends AbstractJmsTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "jms-publish-test-case.xml";
    }

    @Test
    public void minimalConfig() throws Exception
    {
        runFlow("minimalConfig", TEST_PAYLOAD);
        Message message = getJmsTemplate().receive("myQueue");
        assertThat(message, notNullValue());
        assertThat(((TextMessage)message).getText(), is(TEST_PAYLOAD));
    }
}
