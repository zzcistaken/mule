/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule;

import org.mule.api.construct.FlowConstruct;
import org.mule.api.construct.MuleConnectionsBuilder;
import org.mule.tck.junit4.FunctionalTestCase;

import java.util.Collection;

import org.junit.Test;

public class ApplicationNetworkTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "application-network.xml";
    }

    @Test
    public void visitFlows()
    {
        final MuleConnectionsBuilder builder = new MuleConnectionsBuilder();

        final Collection<FlowConstruct> flowConstructs = muleContext.getRegistry().lookupFlowConstructs();

        for (FlowConstruct flowConstruct : flowConstructs)
        {
            flowConstruct.visitForConnections(builder);
        }

        System.out.println(builder);


        // MuleConnectionsBuilder[provided: {HTTP://0.0.0.0:8081 (FROM, true) =[HTTP://google.com:80/ (TO, true) null],
        // HTTP://0.0.0.0:8088 (FROM, true) =[HTTP://facebook.com:80/ (TO, true) null]}]

    }
}
