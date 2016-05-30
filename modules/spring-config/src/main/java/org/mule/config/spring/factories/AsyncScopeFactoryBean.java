package org.mule.config.spring.factories;

import org.mule.api.MuleContext;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.ProcessingStrategy;
import org.mule.processor.AsyncDelegateMessageProcessor;

public class AsyncScopeFactoryBean extends ScopeFactoryBean
{

    protected MuleContext muleContext;
    protected ProcessingStrategy processingStrategy;
    protected String name;

    public void setMuleContext(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

    public void setProcessingStrategy(ProcessingStrategy processingStrategy)
    {
        this.processingStrategy = processingStrategy;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    protected MessageProcessor doGetObject(MessageProcessor messageProcessor)
    {
        AsyncDelegateMessageProcessor delegate = new AsyncDelegateMessageProcessor(messageProcessor,
                processingStrategy, name);
        delegate.setMuleContext(muleContext);
        return delegate;
    }
}
