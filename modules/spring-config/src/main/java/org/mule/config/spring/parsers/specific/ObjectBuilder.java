package org.mule.config.spring.parsers.specific;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Object builder from an XML parsing
 */
public interface ObjectBuilder<T>
{
    void collectAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder);

    <T> Class<T> getFactoryBeanType();

}
