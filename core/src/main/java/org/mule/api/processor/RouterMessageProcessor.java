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

import java.util.List;

import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;

//TODO should this class be merged with ScopeMessageProcessor
public class RouterMessageProcessor implements MessageProcessor, MessageProcessorsOwner, Lifecycle, MuleContextAware, FlowConstructAware
{
    private static transient Logger logger = org.slf4j.LoggerFactory.getLogger(RouterMessageProcessor.class);

    private final MessageProcessor scopeProcessor;
    //TODO still missing logic to execute inner message processors through scope message processor
    private List<MessageProcessor> childMessageProcessors;
    private FlowConstruct flowConstruct;
    private MuleContext context;

    public RouterMessageProcessor(MessageProcessor messageProcessor, List<MessageProcessor> childMessageProcessors)
    {
        this.scopeProcessor = messageProcessor;
        this.childMessageProcessors = childMessageProcessors;
    }

    @Override
    public MuleEvent process(MuleEvent event) throws MuleException
    {
        return scopeProcessor.process(event);
    }

    @Override
    public List<MessageProcessor> getChildMessageProcessors()
    {
        return childMessageProcessors;
    }

    @Override
    public void updateChildren(List<MessageProcessor> messageProcessors)
    {
        this.childMessageProcessors = messageProcessors;
    }

    @Override
    public void dispose()
    {
        LifecycleUtils.disposeAllIfNeeded(childMessageProcessors, logger);
        LifecycleUtils.disposeIfNeeded(scopeProcessor, logger);
    }

    @Override
    public void initialise() throws InitialisationException
    {
        LifecycleUtils.initialiseAllIfNeeded(childMessageProcessors, context, flowConstruct);
        LifecycleUtils.initialiseIfNeeded(scopeProcessor, context, flowConstruct);
    }

    @Override
    public void start() throws MuleException
    {
        LifecycleUtils.startIfNeeded(childMessageProcessors);
        LifecycleUtils.startIfNeeded(scopeProcessor);
    }

    @Override
    public void stop() throws MuleException
    {
        LifecycleUtils.stopIfNeeded(scopeProcessor);
        LifecycleUtils.stopIfNeeded(childMessageProcessors);
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
