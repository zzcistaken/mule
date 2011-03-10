/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.exceptions;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.service.Service;
import org.mule.message.ExceptionMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.tck.probe.PollingProber;
import org.mule.tck.probe.Probe;
import org.mule.tck.probe.Prober;

public class StoppingServiceExceptionStrategyTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/exceptions/stopping-service-exception-strategy-config.xml";
    }

    public void testStopsServiceOnException() throws MuleException, InterruptedException
    {
        final Service service1 = muleContext.getRegistry().lookupService("testService1");

        MuleClient mc = new MuleClient();
        mc.dispatch("vm://in1", "test", null);

        MuleMessage out = mc.request("vm://out1", FunctionalTestCase.RECEIVE_TIMEOUT);
        assertEquals("test", ((ExceptionMessage) out.getPayload()).getPayload());

        Prober prober = new PollingProber(5000, 100);
        prober.check(new Probe()
        {
            public boolean isSatisfied()
            {
                return !service1.isStarted();
            }

            public String describeFailure()
            {
                return "Service was not stopped after processing the exception";
            }
        });
    }
}
