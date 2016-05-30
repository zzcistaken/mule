package org.mule.config.spring.factories;

import org.mule.AbstractAnnotatedObject;
import org.mule.api.processor.DynamicMessageProcessor;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorBuilder;
import org.mule.api.processor.ScopeMessageProcessor;
import org.mule.processor.chain.DefaultMessageProcessorChainBuilder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;

public abstract class ScopeFactoryBean extends AbstractAnnotatedObject implements FactoryBean<ScopeMessageProcessor>
{

    protected List<MessageProcessor> messageProcessors;

    public void setMessageProcessors(List<MessageProcessor> messageProcessors)
    {
        this.messageProcessors = messageProcessors;
    }

    @Override
    public final ScopeMessageProcessor getObject() throws Exception
    {
        DefaultMessageProcessorChainBuilder builder = new DefaultMessageProcessorChainBuilder();
        builder.setName("'async' child chain");

        List<MessageProcessor> dynamicMessageProcessors = new ArrayList<>();
        for (MessageProcessor messageProcessor : messageProcessors)
        {
//            dynamicMessageProcessors.add(new DynamicMessageProcessor(messageProcessor));
            dynamicMessageProcessors.add(messageProcessor);
        }

        for (Object processor : messageProcessors)
        {
            if (processor instanceof MessageProcessor)
            {
                builder.chain((MessageProcessor) processor);
//                builder.chain(new DynamicMessageProcessor((MessageProcessor) processor));
            }
            else if (processor instanceof MessageProcessorBuilder)
            {
                builder.chain((MessageProcessorBuilder) processor);
            }
            else
            {
                throw new IllegalArgumentException(
                        "MessageProcessorBuilder should only have MessageProcessor's or MessageProcessorBuilder's configured");
            }
        }
        MessageProcessor messageProcessor = doGetObject(builder.build());
        return new ScopeMessageProcessor(messageProcessor, dynamicMessageProcessors);
    }

    protected abstract MessageProcessor doGetObject(MessageProcessor messageProcessor);

    @Override
    public Class<?> getObjectType()
    {
        return ScopeMessageProcessor.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
