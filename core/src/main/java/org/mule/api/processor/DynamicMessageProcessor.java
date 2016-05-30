package org.mule.api.processor;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.construct.FlowConstructAware;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Lifecycle;
import org.mule.api.lifecycle.LifecycleUtils;
import org.mule.util.Preconditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicMessageProcessor implements MessageProcessor, MessageProcessorOwner, Lifecycle, MuleContextAware, FlowConstructAware
{
    private final Logger logger = LoggerFactory.getLogger(DynamicMessageProcessor.class);
    private MuleContext context;
    private FlowConstruct flowConstruct;
    protected MessageProcessor delegateProcessor;

    public DynamicMessageProcessor(MessageProcessor delegateProcessor)
    {
        Preconditions.checkArgument((!(delegateProcessor instanceof DynamicMessageProcessor)), "A dynamic message processor cannot have a dynamic message processor delegate");
        this.delegateProcessor = delegateProcessor;
    }

    @Override
    public MuleEvent process(MuleEvent event) throws MuleException
    {
        return delegateProcessor.process(event);
    }

    public void setDelegate(MessageProcessor processor) {
        this.delegateProcessor = processor;
    }

    @Override
    public MessageProcessor getMessageProcessor()
    {
        return delegateProcessor;
    }

    @Override
    public void dispose()
    {
        LifecycleUtils.disposeIfNeeded(delegateProcessor, logger);
    }

    @Override
    public void initialise() throws InitialisationException
    {
        LifecycleUtils.initialiseIfNeeded(delegateProcessor, context, flowConstruct);
    }

    @Override
    public void start() throws MuleException
    {
        LifecycleUtils.startIfNeeded(delegateProcessor);
    }

    @Override
    public void stop() throws MuleException
    {
        LifecycleUtils.stopIfNeeded(delegateProcessor);
    }

    @Override
    public void setFlowConstruct(FlowConstruct flowConstruct)
    {
        this.flowConstruct = flowConstruct;
    }

    @Override
    public void setMuleContext(MuleContext context)
    {
        this.context = context;
    }
}
