/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.activiti.i18n;

import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.transport.activiti.action.InboundActivitiAction;
import org.mule.transport.activiti.action.OutboundActivitiAction;

public class ActivitiMessages extends MessageFactory
{
    private static final ActivitiMessages factory = new ActivitiMessages();
    
    private static final String BUNDLE_PATH = getBundlePath("activiti");

    public static Message failToExecuteInboundAction(InboundActivitiAction<?> action)
    {
        return factory.createMessage(BUNDLE_PATH, 1, action);
    }
    
    public static Message failToExecuteOutboundAction(OutboundActivitiAction<?> action)
    {
        return factory.createMessage(BUNDLE_PATH, 1, action);
    }
}
