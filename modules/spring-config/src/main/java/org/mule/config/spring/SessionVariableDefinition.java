package org.mule.config.spring;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;

public class SessionVariableDefinition  extends AbstractContentDefinition
{

    private String variableName;

    @Override
    public void verify(MuleEvent event) throws MuleException
    {
        verify(event.getSessionVariable(variableName),"Session variable " + variableName);
    }

    public void setVariableName(String variableName)
    {
        this.variableName = variableName;
    }
}
