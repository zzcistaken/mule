package org.mule.config.spring.parsers.specific;

import org.mule.config.spring.factories.ScatterGather2RouterFactoryBean;
import org.mule.config.spring.factories.ScopeFactoryBean;
import org.mule.config.spring.util.ProcessingStrategyUtils;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ScatterGatherObjectBuilder implements ObjectBuilder<ScatterGather2RouterFactoryBean>
{

    @Override
    public void collectAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder)
    {

    }

    @Override
    public Class<ScatterGather2RouterFactoryBean> getFactoryBeanType()
    {
        return ScatterGather2RouterFactoryBean.class;
    }

}
