/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.internal.provider;

import org.mule.api.DefaultMuleException;
import org.mule.api.MuleException;
import org.mule.api.connection.ConnectionException;
import org.mule.api.connection.ConnectionHandlingStrategy;
import org.mule.api.connection.ConnectionHandlingStrategyFactory;
import org.mule.api.connection.ConnectionProvider;
import org.mule.api.connection.ConnectionValidationResult;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.extension.annotation.api.Alias;
import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.jms.api.JmsConnector;
import org.mule.extension.jms.internal.connectionfactory.jndi.JndiConnectionFactory;
import org.mule.extension.jms.internal.operation.JmsConnection;
import org.mule.extension.jms.internal.support.Jms102bSupport;
import org.mule.extension.jms.internal.support.Jms11Support;
import org.mule.extension.jms.internal.support.JmsConstants;
import org.mule.extension.jms.internal.support.JmsSupport;
import org.mule.extension.jms.internal.support.LookupJndiDestination;

import java.util.function.Function;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link org.mule.api.connection.ConnectionProvider} which provides instances of
 * {@link GenericConnectionProvider} from instances of {@link org.mule.extension.jms.api.JmsConnector}
 *
 * @since 4.0
 */
@Alias("generic")
public class GenericConnectionProvider implements ConnectionProvider<JmsConnector, JmsConnection>, Startable, Initialisable, ExceptionListener, Stoppable
{
    private Logger logger = LoggerFactory.getLogger(GenericConnectionProvider.class);

    @Parameter
    private ConnectionFactory connectionFactory;

    @Parameter
    @Optional
    private String username;

    @Parameter
    @Optional
    private String password;

    @Parameter
    @Optional(defaultValue = JmsConstants.JMS_SPECIFICATION_11)
    private String specification;

    @Parameter
    @Optional
    private String clientId;

    private JmsSupport jmsSupport;
    private java.util.Optional<JndiConnectionFactory> jndiConnectionFactoryOptional = java.util.Optional.empty();;
    /**
     * Used to ignore handling of ExceptionListener#onException when in the process of disconnecting.  This is
     * required because the Connector {@link org.mule.api.lifecycle.LifecycleManager} does not include
     * connection/disconnection state.
     */
    private volatile boolean disconnecting;
    private Connection connection;

    @Override
    public JmsConnection connect(JmsConnector jmsConnector) throws ConnectionException
    {
        return new JmsConnection(jmsSupport, connection);
    }

    @Override
    public void disconnect(JmsConnection jmsConnection)
    {

    }

    @Override
    public ConnectionValidationResult validate(JmsConnection jmsConnection)
    {
        return ConnectionValidationResult.success();
    }

    @Override
    public ConnectionHandlingStrategy<JmsConnection> getHandlingStrategy(ConnectionHandlingStrategyFactory<JmsConnector, JmsConnection> handlingStrategyFactory)
    {
        return handlingStrategyFactory.none();
    }

    @Override
    public void start() throws MuleException
    {
        try
        {
            this.connection = createConnection();
            this.connection.start();
        }
        catch (MuleException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new DefaultMuleException(e);
        }
    }

    @Override
    public void initialise() throws InitialisationException
    {
        if (connectionFactory instanceof JndiConnectionFactory)
        {
            jndiConnectionFactoryOptional = java.util.Optional.of((JndiConnectionFactory) connectionFactory);
        }
        createJmsSupport();
    }

    /**
     * A factory method to create various JmsSupport class versions.
     *
     * @see JmsSupport
     * @return JmsSupport instance
     */
    private void createJmsSupport()
    {
        try
        {
            LookupJndiDestination lookupJndiDestination = jndiConnectionFactoryOptional.map( jndiConnectionFactory -> {
                return jndiConnectionFactory.getLookupJndiDestination();
            }).orElse(LookupJndiDestination.NEVER);

            Function<String, Destination> getJndiDestinationFunction = (String destName) -> {return null;};
            if (jndiConnectionFactoryOptional.isPresent())
            {
                getJndiDestinationFunction = jndiConnectionFactoryOptional.get()::getJndiDestination;
            }

            if (JmsConstants.JMS_SPECIFICATION_102B.equals(specification))
            {
                jmsSupport = new Jms102bSupport(lookupJndiDestination, getJndiDestinationFunction);
            }
            else
            {
                jmsSupport = new Jms11Support(lookupJndiDestination, getJndiDestinationFunction);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    protected Connection createConnection() throws Exception
    {
        createConnectionFactory();

        Connection connection;

        if (username != null)
        {
            connection = jmsSupport.createConnection(connectionFactory, username, password);
        }
        else
        {
            connection = jmsSupport.createConnection(connectionFactory);
        }

        if (connection != null)
        {
            // EE-1901: only sets the clientID if it was not already set
            java.util.Optional<String> configuredClientIdOptional = java.util.Optional.ofNullable(clientId);
            if (configuredClientIdOptional.isPresent() && !configuredClientIdOptional.get().equals(connection.getClientID()))
            {
                connection.setClientID(configuredClientIdOptional.get());
            }

            //TODO review embeddedMode improvement
            if (connection.getExceptionListener() == null)
            {
                connection.setExceptionListener(this);
            }
        }
        return connection;
    }

    protected void createConnectionFactory() throws Exception
    {
        //Already created since it's must be configured through the config.
    }


    @Override
    public void onException(JMSException e)
    {
        if (!disconnecting)
        {
            //TODO implement
            //Map<Object, MessageReceiver> receivers = getReceivers();
            //boolean isMultiConsumerReceiver = false;
            //
            //if (!receivers.isEmpty())
            //{
            //    MessageReceiver reciever = receivers.values().iterator().next();
            //    if (reciever instanceof MultiConsumerJmsMessageReceiver)
            //    {
            //        isMultiConsumerReceiver = true;
            //    }
            //}
            //
            //int expectedReceiverCount = isMultiConsumerReceiver ? 1 :
            //                            (getReceivers().size() * getNumberOfConcurrentTransactedReceivers());
            //
            //if (logger.isDebugEnabled())
            //{
            //    logger.debug("About to recycle myself due to remote JMS connection shutdown but need "
            //                 + "to wait for all active receivers to report connection loss. Receiver count: "
            //                 + (receiverReportedExceptionCount.get() + 1) + '/' + expectedReceiverCount);
            //}
            //
            //if (receiverReportedExceptionCount.incrementAndGet() >= expectedReceiverCount)
            //{
            //    receiverReportedExceptionCount.set(0);
            //    muleContext.getExceptionListener().handleException(new ConnectException(jmsException, this));
            //}
        }
    }

    @Override
    public void stop() throws MuleException
    {
        disconnecting = true;
        try
        {
            connection.close();
        }
        catch (Exception e)
        {
            logger.warn(e.getMessage());
            if (logger.isDebugEnabled())
            {
                logger.debug(e.getMessage(), e);
            }
        }
    }

    protected String getUsername()
    {
        return username;
    }

    protected String getPassword()
    {
        return password;
    }

    protected String getSpecification()
    {
        return specification;
    }

    protected String getClientId()
    {
        return clientId;
    }

    protected JmsSupport getJmsSupport()
    {
        return jmsSupport;
    }

    protected ConnectionFactory getConnectionFactory() throws Exception
    {
        return connectionFactory;
    }

    protected void setConnectionFactory(ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }
}
