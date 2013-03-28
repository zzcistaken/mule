package org.mule.config.spring;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;

public class PayloadContentDefinition extends AbstractContentDefinition
{

    public void verify(MuleEvent event) throws MuleException
    {
        verify(event.getMessage().getPayload(),"payload ");
    }

}
