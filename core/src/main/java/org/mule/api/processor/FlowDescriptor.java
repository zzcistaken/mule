package org.mule.api.processor;

public class FlowDescriptor extends MessageProcessorDescriptor
{

    private final String flowName;

    public FlowDescriptor(String flowName, MessageProcessor messageProcessor)
    {
        super(null, messageProcessor);
        this.flowName = flowName;
    }

    @Override
    public String getRoute()
    {
        return flowName;
    }
}
