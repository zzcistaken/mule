package org.mule.config.spring;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageContentDefinition
{

    private List<AbstractContentDefinition> contentDefinitions = Collections.emptyList();
    //TODO this MUST not be here
    protected Map<Object, Object> properties = new HashMap<Object, Object>();

    public void setContentDefinitions(List<AbstractContentDefinition> contentDefinitionList)
    {
        this.contentDefinitions = contentDefinitionList;
    }

    public void verify(MuleEvent event) throws MuleException
    {
        for (AbstractContentDefinition contentDefinition : contentDefinitions)
        {
            contentDefinition.verify(event);
        }
    }

    public void setProperties(Map<Object, Object> properties)
    {
        this.properties = properties;
    }
}
