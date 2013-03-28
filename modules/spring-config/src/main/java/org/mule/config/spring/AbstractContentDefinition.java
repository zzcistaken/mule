package org.mule.config.spring;

import org.mule.api.DefaultMuleException;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;

public abstract class AbstractContentDefinition
{

    private Class type;

    public void setType(Class type)
    {
        this.type = type;
    }

    public void verify(Object value, String valueHolderDescription) throws MuleException
    {
        if (value == null)
        {
            throw new DefaultMuleException(valueHolderDescription + " is required.");
        }
        if (!type.isInstance(value))
        {
            throw new DefaultMuleException(valueHolderDescription + " must be an instance of " + type.getName());
        }
    }

    public abstract void verify(MuleEvent event) throws MuleException;
}
