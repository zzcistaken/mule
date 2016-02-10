/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.provider;

import org.mule.extension.annotation.api.Alias;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.util.ClassUtils;

import javax.jms.ConnectionFactory;

@Alias("active-mq")
public class ActiveMqConnectionProvider extends GenericConnectionProvider
{

    private static final String ACTIVEMQ_CONNECTION_FACTORY_CLASS = "org.apache.activemq.ActiveMQConnectionFactory";
    private static final String DEFAULT_BROKER_URL = "vm://localhost?broker.persistent=false&broker.useJmx=false";

    @Optional(defaultValue = "false")
    private boolean enableXA;

    @Optional(defaultValue = DEFAULT_BROKER_URL)
    private String brokerUrl;

    @Override
    protected void createConnectionFactory() throws Exception
    {
        if (getConnectionFactory() == null)
        {
            setConnectionFactory(getDefaultConnectionFactory());
        }
    }

    protected ConnectionFactory getDefaultConnectionFactory() throws Exception
    {
        ConnectionFactory connectionFactory = (ConnectionFactory)
                ClassUtils.instanciateClass(ACTIVEMQ_CONNECTION_FACTORY_CLASS, brokerUrl);
        applyVendorSpecificConnectionFactoryProperties(connectionFactory);
        return connectionFactory;
    }

    protected void applyVendorSpecificConnectionFactoryProperties(ConnectionFactory connectionFactory)
    {
        //TODO review how to apply this. We are now allowing this configuration per subscriber.
        //try
        //{
        //    Method getRedeliveryPolicyMethod = connectionFactory.getClass().getMethod("getRedeliveryPolicy");
        //    Object redeliveryPolicy = getRedeliveryPolicyMethod.invoke(connectionFactory);
        //    Method setMaximumRedeliveriesMethod = redeliveryPolicy.getClass().getMethod("setMaximumRedeliveries", Integer.TYPE);
        //    int maxRedelivery = getMaxRedelivery();
        //    if (maxRedelivery != REDELIVERY_IGNORE )
        //    {
        //        // redelivery = deliveryCount - 1, but AMQ is considering the first delivery attempt as a redelivery (wrong!). adjust for it
        //        maxRedelivery++;
        //    }
        //    setMaximumRedeliveriesMethod.invoke(redeliveryPolicy, maxRedelivery);
        //}
        //catch (Exception e)
        //{
        //    logger.error("Can not set MaxRedelivery parameter to RedeliveryPolicy " + e);
        //}
    }

}
