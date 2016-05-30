package org.mule.api.processor;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MessageProcessorDescriptor
{

    private final Optional<MessageProcessorDescriptor> parent;
    private final MessageProcessor messageProcessor;
    private List<MessageProcessorDescriptor> children;

    public MessageProcessorDescriptor(MessageProcessorDescriptor parent, MessageProcessor messageProcessor)
    {
        this.parent = Optional.ofNullable(parent);
        this.messageProcessor = messageProcessor;
        this.children = new ArrayList<>();
    }

    public String getRoute()
    {
        Preconditions.checkArgument(parent.isPresent(), "Does not make sense to get the route of a flow");
        return parent.get().getRoute() + "/" + parent.get().getChildIndex(this);
    }

    int getChildIndex(MessageProcessorDescriptor child)
    {
        for (int i = 0; i < children.size(); i++)
        {
            if (child == children.get(i)) {
                return i;
            }
        }
        throw new IllegalArgumentException("MP is not a child of mine");
    }

    public Optional<MessageProcessorDescriptor> getParent()
    {
        return this.parent;
    }

    public List<MessageProcessorDescriptor> getChildren()
    {
        return Collections.unmodifiableList(this.children);
    }

    public void addChild(MessageProcessorDescriptor messageProcessorDescriptor)
    {
        this.children.add(messageProcessorDescriptor);
    }

    public MessageProcessor getMessageProcessor()
    {
        return messageProcessor;
    }

    public void addChildren(List<MessageProcessorDescriptor> childrenMessageProcessorDescriptor)
    {
        this.children = childrenMessageProcessorDescriptor;
    }
}
