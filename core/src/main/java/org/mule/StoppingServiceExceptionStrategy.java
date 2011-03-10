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

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.service.Service;
import org.mule.service.DefaultServiceExceptionStrategy;

/**
 * Adds the ability to stop the service where a exception was produced in
 * order to avoid further messages to be consumed. Service can be restarted
 * once the root problem was fixed.
 */
public class StoppingServiceExceptionStrategy extends DefaultServiceExceptionStrategy
{

    @Override
    public void handleMessagingException(MuleMessage message, Throwable t)
    {
        super.handleMessagingException(message, t);

        final MuleEvent event = RequestContext.getEvent();

        if (event != null)
        {
            Service service = event.getService();
            try
            {
                logger.info("Stopping service '" + service.getName() + "' due to exception");
                service.stop();
            }
            catch (MuleException e)
            {
                logger.error("Unable to stop service '" + service.getName() + "'", e);
            }
        }
    }
}
