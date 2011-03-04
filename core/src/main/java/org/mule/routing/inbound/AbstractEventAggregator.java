/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.routing.inbound;

import org.mule.DefaultMuleEvent;
import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.routing.EventCorrelator;
import org.mule.routing.EventCorrelatorCallback;

/**
 * <code>AbstractEventAggregator</code> will aggregate a set of messages into a
 * single message.
 */

public abstract class AbstractEventAggregator extends SelectiveConsumer
{

    protected EventCorrelator eventCorrelator;

    private int timeout = 0;

    private boolean failOnTimeout = true;

    @Override
    public void initialise() throws InitialisationException
    {
        if (eventCorrelator == null)
        {
            eventCorrelator = new EventCorrelator(getCorrelatorCallback(), getMessageInfoMapping(), muleContext);

            if (timeout != 0)
            {
                eventCorrelator.setTimeout(timeout);
                eventCorrelator.setFailOnTimeout(isFailOnTimeout());

                try
                {
                    eventCorrelator.start();
                }
                catch (MuleException e)
                {
                    throw new InitialisationException(e, this);
                }
            }
        }

        super.initialise();
    }

    @Override
    public void dispose()
    {
        try
        {
            if (eventCorrelator != null)
            {
                eventCorrelator.stop();
            }
        }
        catch (MuleException e)
        {
            // Ignoring
        }

        super.dispose();
    }

    protected abstract EventCorrelatorCallback getCorrelatorCallback();

    @Override
    public MuleEvent[] process(MuleEvent event) throws MessagingException
    {
        MuleMessage msg = eventCorrelator.process(event);
        if (msg == null)
        {
            return null;
        }
        MuleEvent[] result = new MuleEvent[] {new DefaultMuleEvent(msg, event)};
        return result;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public boolean isFailOnTimeout()
    {
        return failOnTimeout;
    }

    public void setFailOnTimeout(boolean failOnTimeout)
    {
        this.failOnTimeout = failOnTimeout;
    }
}
