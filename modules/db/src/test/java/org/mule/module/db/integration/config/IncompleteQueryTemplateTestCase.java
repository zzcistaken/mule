/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.db.integration.config;

import org.junit.Test;

public class IncompleteQueryTemplateTestCase extends AbstractConfigurationErrorTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "integration/config/incomplete-query-template-config.xml";
    }

    @Test
    public void requiresUserAttribute() throws Exception
    {
        assertConfigurationError(
                "Able to define a query template with named params without defining parameter types",
                "Missing declaration for parameter: id");
    }
}
