package org.mule.transport.cxf.messagedispatcher;

/**
 *
 */
class CallAndExpectWrongNumberOfArguments extends AbstractCallAndExpectIllegalArgumentException
{
    public CallAndExpectWrongNumberOfArguments(String outputEndpointName, Object payload)
    {
        super(outputEndpointName, payload);
    }

    @Override
    public String expectedIllegalArgumentExceptionMessage()
    {
        return "wrong number of arguments";
    }
}