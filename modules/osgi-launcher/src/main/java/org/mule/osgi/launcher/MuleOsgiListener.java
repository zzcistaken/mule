/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.launcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.framework.Logger;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

/**
 *
 */
public class MuleOsgiListener implements FrameworkListener
{

    protected transient Log logger = LogFactory.getLog(MuleOsgiListener.class);

    @Override
    public void frameworkEvent(FrameworkEvent event)
    {
        int eventType = event.getType();
        String msg = getFrameworkEventMessage(eventType);
        int level = (eventType == FrameworkEvent.ERROR) ? Logger.LOG_ERROR : Logger.LOG_WARNING;
        if (msg != null)
        {
            log(level, msg, event.getThrowable());
        }
        else
        {
            log(level, "Unknown framework event: " + event);
        }
    }

    private void log(int level, String msg, Throwable throwable)
    {
        if (throwable != null)
        {
            logger.debug(msg, throwable);
        }
        else
        {
            logger.debug(msg);
        }
    }

    private void log(int level, String s)
    {
        System.out.println(s);
    }

    private String getFrameworkEventMessage(int event)
    {
        switch (event)
        {
            case FrameworkEvent.ERROR:
                return "FrameworkEvent: ERROR";
            case FrameworkEvent.INFO:
                return "FrameworkEvent INFO";
            case FrameworkEvent.PACKAGES_REFRESHED:
                return "FrameworkEvent: PACKAGE REFRESHED";
            case FrameworkEvent.STARTED:
                return "FrameworkEvent: STARTED";
            case FrameworkEvent.STARTLEVEL_CHANGED:
                return "FrameworkEvent: STARTLEVEL CHANGED";
            case FrameworkEvent.WARNING:
                return "FrameworkEvent: WARNING";
            case FrameworkEvent.STOPPED:
                return "FrameworkEvent: STOPPED";
            default:
                return "UNKNOWN EVENT: " + event;
        }
    }
}
