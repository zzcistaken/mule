/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.jms.api;

import org.mule.extension.api.introspection.declaration.spi.Describer;
import org.mule.extension.jms.api.JmsConnector;
import org.mule.module.extension.DescriberSupport;

/**
 * {@link Describer} implementation for the
 * {@link JmsConnector}
 *
 * @since 4.0
 */
public class JmsConnectorDescriber extends DescriberSupport
{

    public JmsConnectorDescriber()
    {
        super(JmsConnector.class);
    }
}
