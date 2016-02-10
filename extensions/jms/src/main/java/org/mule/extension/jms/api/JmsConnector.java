/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.api;

import org.mule.api.MuleContext;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.extension.annotation.api.Extension;
import org.mule.extension.annotation.api.Operations;
import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.Sources;
import org.mule.extension.annotation.api.capability.Xml;
import org.mule.extension.annotation.api.connector.Providers;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.jms.internal.provider.ActiveMqConnectionProvider;
import org.mule.extension.jms.internal.provider.GenericConnectionProvider;
import org.mule.extension.jms.internal.operation.JmsConsume;
import org.mule.extension.jms.internal.operation.JmsPublish;
import org.mule.extension.jms.internal.source.Subscriber;

import javax.inject.Inject;

/**
 * @since 4.0
 */
@Extension(name = "JMS Connector", description = "Connector to connector to any JMS broker")
@Operations({JmsConsume.class, JmsPublish.class})
@Providers({GenericConnectionProvider.class, ActiveMqConnectionProvider.class})
@Sources(Subscriber.class)
@Xml(schemaLocation = "http://www.mulesoft.org/schema/mule/jms", namespace = "jms", schemaVersion = "4.0")
public class JmsConnector implements Initialisable
{

    @Inject
    private MuleContext muleContext;

    @Parameter
    @Optional(defaultValue = "0")
    private Integer maxRedelivery;

    @Parameter
    @Optional(defaultValue = "AUTO")
    private AckMode ackMode;

    @Parameter
    @Optional(defaultValue = "false")
    private boolean durableTopicSubscriber;

    @Parameter
    @Optional(defaultValue = "true")
    private boolean persistentDelivery;

    @Parameter
    @Optional(defaultValue = "4")
    private Integer priority;

    @Parameter
    @Optional(defaultValue = "0")
    private Long timeToLive;

    @Parameter
    @Optional(defaultValue = "false")
    private boolean noLocal;

    public Integer getMaxRedelivery()
    {
        return maxRedelivery;
    }

    public AckMode getAckMode()
    {
        return ackMode;
    }

    public boolean isDurableTopicSubscriber()
    {
        return durableTopicSubscriber;
    }

    public boolean isPersistentDelivery()
    {
        return persistentDelivery;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public Long getTimeToLive()
    {
        return timeToLive;
    }

    public boolean isNoLocal()
    {
        return noLocal;
    }

    @Override
    public void initialise() throws InitialisationException
    {
    }

}
