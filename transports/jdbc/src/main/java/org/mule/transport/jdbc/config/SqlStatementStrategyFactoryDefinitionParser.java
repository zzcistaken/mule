/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.jdbc.config;

/**
 * Parser for sqlStatementStrategyFactory property.
 */
public class SqlStatementStrategyFactoryDefinitionParser extends AbstractJdbcFactoryBeanParser
{

    @Override
    protected String getPropertyName()
    {
        return JdbcNamespaceHandler.SQL_STATEMENT_FACTORY_PROPERTY;
    }

}
