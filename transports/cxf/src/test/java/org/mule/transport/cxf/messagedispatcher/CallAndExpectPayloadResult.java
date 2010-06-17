package org.mule.transport.cxf.messagedispatcher;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import junit.framework.TestCase;

/**
 *
 */
class CallAndExpectPayloadResult implements CallAndExpect
{
    private Object expectedPayloadResult;
    private String outputEndpointName;
    private Object payload;

    public CallAndExpectPayloadResult(String outputEndpointName,
                                      Object payload,
                                      Object expectedPayloadResult)
    {
        this.expectedPayloadResult = expectedPayloadResult;
        this.outputEndpointName = outputEndpointName;
        this.payload = payload;
    }

    public void callEndpointAndExecuteAsserts() throws MuleException
    {
        MuleClient client = new MuleClient();
        MuleMessage result = client.send(outputEndpointName, payload, null);

        TestCase.assertEquals(here(), expectedPayloadResult, result.getPayload());
    }

    private String here()
    {
        return "In [" + outputEndpointName + "," + payload + "," + expectedPayloadResult + "]";
    }
}