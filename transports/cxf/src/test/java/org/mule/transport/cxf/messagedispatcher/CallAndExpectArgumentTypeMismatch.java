package org.mule.transport.cxf.messagedispatcher;

/**
 *
 */
class CallAndExpectArgumentTypeMismatch extends AbstractCallAndExpectIllegalArgumentException
{
    public CallAndExpectArgumentTypeMismatch(String outputEndpointName, Object payload)
    {
        super(outputEndpointName, payload);
    }

    @Override
    public String expectedIllegalArgumentExceptionMessage()
    {
        return "argument type mismatch";
    }
}