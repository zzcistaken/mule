package org.mule.config.spring.factories;

import org.mule.api.MuleContext;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.processor.FlowDescriptor;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorDescriptor;
import org.mule.api.processor.MessageProcessorsOwner;
import org.mule.api.processor.ProcessingStrategy;
import org.mule.api.source.MessageSource;
import org.mule.construct.AbstractFlowConstruct;
import org.mule.construct.Flow;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.FactoryBean;

public class FlowFactoryBean implements FactoryBean<Flow>
{

    private String name;
    private ProcessingStrategy processingStrategy;
    private List<MessageProcessor> messageProcessors = Collections.emptyList();
    private MessageSource messageSource;
    private MuleContext muleContext;
    private String initialState = AbstractFlowConstruct.INITIAL_STATE_STARTED;
    private MessagingExceptionHandler exceptionListener;

    public void setName(String name)
    {
        this.name = name;
    }

    public void setExceptionListener(MessagingExceptionHandler exceptionListener)
    {
        this.exceptionListener = exceptionListener;
    }

    public void setProcessingStrategy(ProcessingStrategy processingStrategy)
    {
        this.processingStrategy = processingStrategy;
    }

    public void setMessageProcessors(List<MessageProcessor> messageProcessors)
    {
        this.messageProcessors = messageProcessors;
    }

    public void setMessageSource(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    public void setMuleContext(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

    public void setInitialState(String initialState)
    {
        this.initialState = initialState;
    }

    @Override
    public Flow getObject() throws Exception
    {
        Flow flow = new Flow(name, muleContext);
        flow.setProcessingStrategy(processingStrategy);
        flow.setDescriptor(createFlowDescriptor(flow, messageProcessors));
        flow.setMessageProcessors(messageProcessors);
        flow.setMessageSource(messageSource);
        flow.setExceptionListener(exceptionListener);

        debugFlowDescriptor(createFlowDescriptor(flow, messageProcessors));

        return flow;
    }

    private void debugFlowDescriptor(MessageProcessorDescriptor flowDescriptor)
    {
        System.out.println(flowDescriptor.getRoute());
        for (MessageProcessorDescriptor child : flowDescriptor.getChildren()   )
        {
            debugFlowDescriptor(child);
        }
    }

    /**
     * Gets the localizable parent and the children and updates the parent to contain the localizable children
     *
     * @param flow
     * @param pipelineProcessors
     */
    private FlowDescriptor createFlowDescriptor(Flow flow, List<? extends MessageProcessor> pipelineProcessors)
    {
        FlowDescriptor flowDescriptor = new FlowDescriptor(name, flow);
        updateProcessorDescriptorWithChildren(flowDescriptor, pipelineProcessors);
        return flowDescriptor;
    }

    private void updateProcessorDescriptorWithChildren(MessageProcessorDescriptor parentMessageProcessor, List<? extends MessageProcessor> childrenMessageProcessors)
    {
        childrenMessageProcessors.stream().map(messageProcessor -> {
            MessageProcessorDescriptor childMessageProcessorDescriptor = new MessageProcessorDescriptor(parentMessageProcessor, messageProcessor);
            if (messageProcessor instanceof MessageProcessorsOwner) {
                MessageProcessorsOwner messageProcessorsOwner = (MessageProcessorsOwner) messageProcessor;
                List<MessageProcessor> childChildrenMessageProcessorDescriptor = messageProcessorsOwner.getChildMessageProcessors();
                updateProcessorDescriptorWithChildren(childMessageProcessorDescriptor, childChildrenMessageProcessorDescriptor);
            }
            parentMessageProcessor.addChild(childMessageProcessorDescriptor);
            return childMessageProcessorDescriptor;
        }).collect(Collectors.toList());
    }


    @Override
    public Class<?> getObjectType()
    {
        return Flow.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
