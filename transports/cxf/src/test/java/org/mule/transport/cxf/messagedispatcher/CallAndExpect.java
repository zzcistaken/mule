package org.mule.transport.cxf.messagedispatcher;

import org.mule.api.MuleException;

/**
 *
 */
interface CallAndExpect
{
    void callEndpointAndExecuteAsserts() throws MuleException;
}