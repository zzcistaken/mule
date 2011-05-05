/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.routing.outbound;

import org.mule.DefaultMuleMessage;
import org.mule.api.MessagingException;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.MuleSession;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.routing.CouldNotRouteOutboundMessageException;
import org.mule.api.routing.RoutePathNotFoundException;
import org.mule.api.routing.RoutingException;
import org.mule.config.i18n.CoreMessages;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a router that sequentially routes a given message to the list of
 * registered endpoints and returns the aggregate responses as the result.
 * Aggregate response is built using the partial responses obtained from
 * synchronous endpoints.
 * The routing process can be stopped after receiving a partial response.
 */
public abstract class AbstractSequenceRouter extends FilteringOutboundRouter
{

    public MuleMessage route(MuleMessage message, MuleSession session)
            throws RoutingException
    {
        if (endpoints == null || endpoints.size() == 0)
        {
            throw new RoutePathNotFoundException(CoreMessages.noEndpointsForRouter(), message, null);
        }
        if (enableCorrelation != ENABLE_CORRELATION_NEVER)
        {
            boolean correlationSet = message.getCorrelationId() != null;
            if (correlationSet && (enableCorrelation == ENABLE_CORRELATION_IF_NOT_SET))
            {
                logger.debug("CorrelationId is already set, not setting Correlation group size");
            }
            else
            {
                // the correlationId will be set by the AbstractOutboundRouter
                message.setCorrelationGroupSize(endpoints.size());
            }
        }

        List results = new ArrayList(endpoints.size());
        try
        {
            OutboundEndpoint endpoint;
            for (int i = 0; i < endpoints.size(); i++)
            {
                endpoint = (OutboundEndpoint) endpoints.get(i);
                if (endpoint.getFilter() == null || (endpoint.getFilter() != null && endpoint.getFilter().accept(message)))
                {
                    if (((DefaultMuleMessage) message).isConsumable())
                    {
                        throw new MessagingException(
                                CoreMessages.cannotCopyStreamPayload(message.getPayload().getClass().getName()),
                                message);
                    }

                    MuleMessage clonedMessage = new DefaultMuleMessage(message.getPayload(), message);
                    if (endpoint.isSynchronous())
                    {
                        MuleMessage partialResponse = send(session, clonedMessage, endpoint);
                        results.add(partialResponse);

                        if (!continueRoutingMessageAfter(partialResponse))
                        {
                            break;
                        }
                    }
                    else
                    {
                        dispatch(session, clonedMessage, endpoint);
                    }
                }
            }
        }
        catch (MuleException e)
        {
            throw new CouldNotRouteOutboundMessageException(message, (ImmutableEndpoint) endpoints.get(0), e);
        }
        return resultsHandler.aggregateResults(results, message);
    }

    /**
     * Lets subclasses decide if the routing of a given message should continue
     * or not after receiving a given response from a synchronous endpoint.
     *
     * @param response the last received response
     * @return true if must continue and false otherwise.
     * @throws MuleException when the router should stop processing throwing an
     *                       exception without returning any results to the caller.
     */
    protected abstract boolean continueRoutingMessageAfter(MuleMessage response) throws MuleException;
}
