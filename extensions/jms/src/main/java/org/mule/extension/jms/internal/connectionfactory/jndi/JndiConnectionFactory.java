/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.connectionfactory.jndi;

import org.mule.api.MuleRuntimeException;
import org.mule.api.transaction.Transaction;
import org.mule.api.transaction.TransactionException;
import org.mule.config.i18n.MessageFactory;
import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.jms.internal.JmsConnectorException;
import org.mule.extension.jms.internal.support.LookupJndiDestination;
import org.mule.transaction.TransactionCoordination;

import java.text.MessageFormat;

import javax.jms.Destination;
import javax.naming.CommunicationException;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JndiConnectionFactory
{

    private static final Logger logger = LoggerFactory.getLogger(JndiConnectionFactory.class);

    @Parameter
    @Optional(defaultValue = "NEVER")
    private LookupJndiDestination lookupJndiDestination;
    private JndiNameResolver jndiNameResolver;

    public LookupJndiDestination getLookupJndiDestination()
    {
        return lookupJndiDestination;
    }

    public Destination getJndiDestination(String name)
    {
        Object temp;
        try
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(MessageFormat.format("Looking up {0} from JNDI", name));
            }
            temp = lookupFromJndi(name);
        }
        catch (NamingException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(e.getMessage(), e);
            }
            String message = MessageFormat.format("Failed to look up destination {0}. Reason: {1}",
                                                  name, e.getMessage());
            throw new JmsConnectorException(message);
        }

        if (temp != null)
        {
            if (temp instanceof Destination)
            {
                return (Destination) temp;
            }
        }
        return null;
    }

    private Object lookupFromJndi(String jndiName) throws NamingException
    {
        try
        {
            return jndiNameResolver.lookup(jndiName);
        }
        catch (CommunicationException ce)
        {
            try
            {
                final Transaction tx = TransactionCoordination.getInstance().getTransaction();
                if (tx != null)
                {
                    tx.setRollbackOnly();
                }
            }
            catch (TransactionException e)
            {
                throw new MuleRuntimeException(
                        MessageFactory.createStaticMessage("Failed to mark transaction for rollback: "), e);
            }

            // re-throw
            throw ce;
        }
    }
}
