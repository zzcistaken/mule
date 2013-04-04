package org.mule.config.spring;

import org.mule.config.spring.parsers.delegate.ParentContextDefinitionParser;
import org.mule.config.spring.parsers.generic.ChildDefinitionParser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 */
public class TemplateStageDefinitionParser extends ChildDefinitionParser
{

    public TemplateStageDefinitionParser()
    {
        super("messageProcessor",TemplateStage.class);
        addIgnored("provided-content");
        addIgnored("required-content");
    }

    @Override
    protected void checkElementNameUnique(Element element)
    {
        return;
    }

    @Override
    public String getPropertyName(Element e)
    {
        boolean isAbstractFlow = false;
        Node parentNode = e.getParentNode();
        while (parentNode != null)
        {
            try
            {
                if (parentNode instanceof Element)
                {
                    Element parentElement = (Element)parentNode;
                    try
                    {
                        BeanDefinition parentBeanDefinition = getRegistry().getBeanDefinition(parentElement.getAttribute(ATTRIBUTE_NAME));
                        if (parentBeanDefinition.isAbstract())
                        {
                            isAbstractFlow = true;
                            break;
                        }
                    }
                    catch (Exception ex)
                    {
                        if (parentNode.getLocalName().equals("flow"))
                        {
                            if ((parentElement).hasAttribute("abstract"))
                            {
                                if (parentElement.getAttribute("abstract").equals("true"))
                                {
                                    isAbstractFlow = true;
                                }
                            }
                            break;
                        }
                    }
                }
                parentNode = parentNode.getParentNode();
            }
            catch (IllegalStateException ex)
            {
                break;
            }
        }
        return (isAbstractFlow ? "messageProcessor" : "templateStage");
    }
}
