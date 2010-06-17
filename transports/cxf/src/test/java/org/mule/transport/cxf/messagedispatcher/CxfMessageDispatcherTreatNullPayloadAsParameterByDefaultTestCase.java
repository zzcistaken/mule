/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.cxf.messagedispatcher;

import static org.mule.transport.cxf.messagedispatcher.CxfMessageDispatcherTestConstants.emptyOjbectArrayPayload;
import static org.mule.transport.cxf.messagedispatcher.CxfMessageDispatcherTestConstants.greetMeOutEndpointName;
import static org.mule.transport.cxf.messagedispatcher.CxfMessageDispatcherTestConstants.nullPayload;
import static org.mule.transport.cxf.messagedispatcher.CxfMessageDispatcherTestConstants.objectPayload;
import static org.mule.transport.cxf.messagedispatcher.CxfMessageDispatcherTestConstants.sayHiOutEndpointName;
import static org.mule.transport.cxf.messagedispatcher.CxfMessageDispatcherTestConstants.strArrayPayload;
import static org.mule.transport.cxf.messagedispatcher.CxfMessageDispatcherTestConstants.strArrayPayloadResult;
import static org.mule.transport.cxf.messagedispatcher.CxfMessageDispatcherTestConstants.strPayload;
import static org.mule.transport.cxf.messagedispatcher.CxfMessageDispatcherTestConstants.strPayloadResult;

import org.mule.tck.FunctionalTestCase;

/**
 * This tests the payloadToArguments attribute on the cxf outbound endpoints for the
 * default case (when it is not supplied).
 */
public class CxfMessageDispatcherTreatNullPayloadAsParameterByDefaultTestCase extends FunctionalTestCase
{
    public void testRunAllScenarios() throws Exception
    {
        CallAndExpect[] callAndExpectArray = {
            new CallAndExpectArgumentTypeMismatch(greetMeOutEndpointName, nullPayload),
            new CallAndExpectArgumentTypeMismatch(greetMeOutEndpointName, objectPayload),
            new CallAndExpectPayloadResult(greetMeOutEndpointName, strPayload, strPayloadResult),
            new CallAndExpectPayloadResult(greetMeOutEndpointName, strArrayPayload, strArrayPayloadResult),
            new CallAndExpectWrongNumberOfArguments(greetMeOutEndpointName, emptyOjbectArrayPayload),

            new CallAndExpectWrongNumberOfArguments(sayHiOutEndpointName, nullPayload),
            new CallAndExpectWrongNumberOfArguments(sayHiOutEndpointName, objectPayload),
            new CallAndExpectWrongNumberOfArguments(sayHiOutEndpointName, strPayload),
            new CallAndExpectWrongNumberOfArguments(sayHiOutEndpointName, strArrayPayload),
            new CallAndExpectPayloadResult(sayHiOutEndpointName, emptyOjbectArrayPayload, "Bonjour")};

        for (CallAndExpect callAndExpect : callAndExpectArray)
        {
            callAndExpect.callEndpointAndExecuteAsserts();
        }
    }

    @Override
    protected String getConfigResources()
    {
        return "messagedispatcher/null-payload-add-as-parameter-by-default.xml";
    }
}
