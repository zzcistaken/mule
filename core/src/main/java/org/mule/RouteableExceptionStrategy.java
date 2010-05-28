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

import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.routing.OutboundRouter;
import org.mule.message.DefaultExceptionPayload;
import org.mule.transport.NullPayload;
import org.mule.util.ObjectUtils;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * <code>RouteableExceptionStrategy</code> allows transforming and routing exceptions
 * to outbound routers. This exception strategy does not take into account any
 * defined endpoints in its instance variable.
 * 
 * @author estebanroblesluna
 * @since 2.2.6
 */
public class RouteableExceptionStrategy extends AbstractExceptionListener
{
    private OutboundRouter router;

    private boolean stopFurtherProcessing = true;

    /**
     * {@inheritDoc}
     */
    public void exceptionThrown(Exception e)
    {
        int currentRootExceptionHashCode = 0;
        int originalRootExceptionHashCode = 0;
        MuleEvent event = null;
        MuleMessage msg = null;

        try
        {
            logger.info("****++******Alternate Exception Strategy******++*******");
            logger.info("Current Thread = " + Thread.currentThread().toString());

            event = RequestContext.getEvent();
            if (event != null && event.getService() != null)
            {
                String serviceName = event.getService().getName();
                logger.info("serviceName = " + serviceName);

                int eventHashCode = event.hashCode();
                logger.info("eventHashCode = " + eventHashCode);
            }

            if (event != null && event.isStopFurtherProcessing())
            {
                logger.info("MuleEvent stop further processing has been set, This is probably the same exception being routed again. no Exception routing will be performed.\n"
                            + e);
                return;
            }

            Throwable root = ExceptionUtils.getRootCause(e);
            currentRootExceptionHashCode = root == null ? -1 : root.hashCode();

            msg = event == null ? null : event.getMessage();

            if (msg != null)
            {
                int msgHashCode = msg.hashCode();
                logger.info("msgHashCode = " + msgHashCode);

                if (msg.getExceptionPayload() != null)
                {
                    Throwable t = msg.getExceptionPayload().getRootException();
                    if (t != null && t.hashCode() == currentRootExceptionHashCode)
                    {
                        logger.info("*#*#*#*#*");
                        logger.info("This error has already been handeled, returning without doing anything: "
                                    + e.getMessage());
                        logger.info("*#*#*#*#*");
                        originalRootExceptionHashCode = currentRootExceptionHashCode;
                        return;
                    }
                }

                originalRootExceptionHashCode = msg.getIntProperty("RootExceptionHashCode", 0);

                logger.info("Original RootExceptionHashCode: " + originalRootExceptionHashCode);
                logger.info("Current  RootExceptionHashCode: " + currentRootExceptionHashCode);

                if (originalRootExceptionHashCode == 0)
                {
                    msg.setIntProperty("RootExceptionHashCode", currentRootExceptionHashCode);
                    originalRootExceptionHashCode = currentRootExceptionHashCode;
                }
                else if (originalRootExceptionHashCode == currentRootExceptionHashCode)
                {
                    logger.info("*#*#*#*#*");
                    logger.info("This error has already been handeled, returning without doing anything: "
                                + e.getMessage());
                    logger.info("*#*#*#*#*");
                    return;
                }
                else
                {
                    msg.setIntProperty("RootExceptionHashCode", currentRootExceptionHashCode);
                }
            }

            logger.debug(e.getMessage());

            StackTraceElement[] st = e.getStackTrace();
            for (int i = 0; i < st.length; i++)
            {
                if (st[i].getClassName().equals("org.mule.AlternateExceptionStrategy"))
                {
                    logger.warn("*#*#*#*#*");
                    logger.warn("Recursive error in AlternateExceptionStrategy " + e);
                    logger.warn("*#*#*#*#*");
                    return;
                }
                logger.debug(st[i].toString());
            }
            super.exceptionThrown(e);

        }
        catch (Throwable t)
        {
            logFatal(msg, t);
        }
        finally
        {
            if (event != null && this.stopFurtherProcessing) event.setStopFurtherProcessing(true);

            if (msg != null && currentRootExceptionHashCode != 0
                && currentRootExceptionHashCode != originalRootExceptionHashCode)
                msg.setIntProperty("RootExceptionHashCode", currentRootExceptionHashCode);

            logger.info("****__******Alternate Exception Strategy******__*******");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void handleMessagingException(MuleMessage message, Throwable t)
    {
        defaultHandler(message, t);
        routeException(getMessageFromContext(message), null, t);
    }

    /**
     * {@inheritDoc}
     */
    public void handleRoutingException(MuleMessage message, ImmutableEndpoint endpoint, Throwable t)
    {
        defaultHandler(message, t);
        routeException(getMessageFromContext(message), endpoint, t);
    }

    /**
     * {@inheritDoc}
     */
    public void handleLifecycleException(Object component, Throwable t)
    {
        logger.error("The object that failed is: \n" + ObjectUtils.toString(component, "null"));
        handleStandardException(t);
    }

    /**
     * {@inheritDoc}
     */
    public void handleStandardException(Throwable t)
    {
        handleTransaction(t);
        if (RequestContext.getEvent() != null)
        {
            handleMessagingException(RequestContext.getEvent().getMessage(), t);
        }
        else
        {
            logger.info("There is no current event available, routing Null message with the exception");
            handleMessagingException(new DefaultMuleMessage(NullPayload.getInstance()), t);
        }
    }

    protected void defaultHandler(MuleMessage message, Throwable t)
    {
        if (RequestContext.getEvent() != null && RequestContext.getEvent().getMessage() != null)
        {
            RequestContext.getEvent().getMessage().setExceptionPayload(new DefaultExceptionPayload(t));
        }

        if (message != null) message.setExceptionPayload(new DefaultExceptionPayload(t));
    }

    protected MuleMessage getMessageFromContext(MuleMessage message)
    {
        if (RequestContext.getEvent() != null)
        {
            return RequestContext.getEvent().getMessage();
        }
        else if (message != null)
        {
            return message;
        }
        else
        {
            return new DefaultMuleMessage(NullPayload.getInstance());
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void routeException(MuleMessage msg, ImmutableEndpoint failedEndpoint, Throwable t)
    {
        MuleMessage contextMsg = null;
        MuleEvent exceptionEvent = RequestContext.getEvent();
        contextMsg = exceptionEvent == null ? msg : exceptionEvent.getMessage();

        if (contextMsg == null)
        {
            contextMsg = new DefaultMuleMessage(NullPayload.getInstance());
            contextMsg.setExceptionPayload(new DefaultExceptionPayload(t));
        }

        if (exceptionEvent == null)
        {
            exceptionEvent = new DefaultMuleEvent(contextMsg, failedEndpoint, new DefaultMuleSession(
                muleContext), true);
        }

        // copy the message
        DefaultMuleMessage messageCopy = new DefaultMuleMessage(contextMsg.getPayload(), contextMsg);

        // route the message
        try
        {
            router.route(messageCopy, exceptionEvent.getSession());
        }
        catch (MessagingException e)
        {
            logFatal(messageCopy, e);
        }
    }

    public OutboundRouter getRouter()
    {
        return router;
    }

    public void setRouter(OutboundRouter router)
    {
        this.router = router;
    }

    public boolean isStopFurtherProcessing()
    {
        return stopFurtherProcessing;
    }

    public void setStopFurtherProcessing(boolean stopFurtherProcessing)
    {
        this.stopFurtherProcessing = stopFurtherProcessing;
    }
}
