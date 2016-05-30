package org.mule.config.spring.parsers.specific;

import org.mule.api.config.MuleProperties;
import org.mule.config.spring.factories.AsyncScopeFactoryBean;
import org.mule.config.spring.util.ProcessingStrategyUtils;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class AsyncObjectBuilder implements ObjectBuilder<AsyncScopeFactoryBean>
{

    @Override
    public void collectAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder)
    {
        ProcessingStrategyUtils.configureProcessingStrategy(element, builder,
                ProcessingStrategyUtils.QUEUED_ASYNC_PROCESSING_STRATEGY);
        builder.addPropertyReference("muleContext", MuleProperties.OBJECT_MULE_CONTEXT);
    }

    @Override
    public Class<AsyncScopeFactoryBean> getFactoryBeanType()
    {
        return AsyncScopeFactoryBean.class;
    }

}
