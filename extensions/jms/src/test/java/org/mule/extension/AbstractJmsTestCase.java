/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import org.mule.functional.junit4.ExtensionFunctionalTestCase;

import org.springframework.jms.core.JmsTemplate;

public class AbstractJmsTestCase extends ExtensionFunctionalTestCase
{

    public static final String DEFAULT_TEST_CONNECTION_FACTORY_NAME = "connectionFactory";

    private JmsTemplate jmsTemplate;

    public JmsTemplate getJmsTemplate()
    {
        if (jmsTemplate == null)
        {
            jmsTemplate = new JmsTemplate(muleContext.getRegistry().get(DEFAULT_TEST_CONNECTION_FACTORY_NAME));
        }
        return jmsTemplate;
    }

    public void sendMessageToQueue(String destination, Object messageContent)
    {
        getJmsTemplate().convertAndSend(destination, messageContent);
    }


}
