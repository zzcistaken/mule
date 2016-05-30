package org.mule.config.spring;

import org.mule.AbstractAnnotatedObject;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.RouterMessageProcessor;

import java.util.List;

import org.springframework.beans.factory.FactoryBean;

public abstract class RouterFactoryBean extends AbstractAnnotatedObject implements FactoryBean<RouterMessageProcessor>
{

    private List<MessageProcessor> messageProcessors;

    public void setMessageProcessors(List<MessageProcessor> messageProcessors)
    {
        this.messageProcessors = messageProcessors;
    }

    @Override
    public RouterMessageProcessor getObject() throws Exception
    {
        MessageProcessor messageProcessor = doGetObject(messageProcessors);
        return new RouterMessageProcessor(messageProcessor, messageProcessors);
    }

    protected abstract MessageProcessor doGetObject(List<MessageProcessor> messageProcessors) throws Exception;

    @Override
    public Class<?> getObjectType()
    {
        return RouterMessageProcessor.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
