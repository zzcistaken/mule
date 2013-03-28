package org.mule.config.spring;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;

/**
 *
 */
public class PropertyDefinition extends AbstractContentDefinition
{

    private String propertyName;

    @Override
    public void verify(MuleEvent event) throws MuleException
    {
        verify(event.getMessage().getOutboundProperty(propertyName),"Outbound property " + propertyName);
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }
}
