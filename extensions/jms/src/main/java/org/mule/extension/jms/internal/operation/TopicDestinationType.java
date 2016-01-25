/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.operation;

import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.param.Optional;

public class TopicDestinationType implements DestinationType
{

    //TODO we need to use the config value if not defined here
    @Parameter
    @Optional
    private boolean useDurableTopicSubscription;

    //TODO we need to use the config value if not defined here
    @Parameter
    @Optional
    private String durableSubscriptionName;

    @Override
    public boolean isTopic()
    {
        return true;
    }

    @Override
    public boolean useDurableTopicSubscription()
    {
        return useDurableTopicSubscription;
    }

    @Override
    public String getDurableSubscriptionName()
    {
        return durableSubscriptionName;
    }
}
