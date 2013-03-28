package org.mule.config.spring;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;

import java.lang.String;

public class FlowVariableDefinition extends AbstractContentDefinition
{
    private String variableName;

    public void verify(MuleEvent event) throws MuleException
    {
        verify(event.getFlowVariable(variableName),"flow variable " + variableName);
    }

    public void setVariableName(String variableName)
    {
        this.variableName = variableName;
    }

}
