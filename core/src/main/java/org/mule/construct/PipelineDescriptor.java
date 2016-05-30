package org.mule.construct;

import org.mule.api.processor.MessageProcessorDescriptor;

import java.util.List;

/**
 * Descriptor for a pipeline of message processors
 */
public class PipelineDescriptor
{

    private List<MessageProcessorDescriptor> messageProcessorDescriptors;

    public PipelineDescriptor(List<MessageProcessorDescriptor> messageProcessorDescriptors)
    {
        this.messageProcessorDescriptors = messageProcessorDescriptors;
    }

    public List<MessageProcessorDescriptor> getMessageProcessorDescriptors()
    {
        return this.messageProcessorDescriptors;
    }

}
