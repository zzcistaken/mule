package org.mule.config.spring;

import org.mule.config.spring.parsers.delegate.ParentContextDefinitionParser;
import org.mule.config.spring.parsers.generic.ChildDefinitionParser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 */
public class TemplateStageNoUniqueName extends ChildDefinitionParser
{

    public TemplateStageNoUniqueName()
    {
        super("messageProcessor",TemplateStage.class);
        addIgnored("documentation");
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
                BeanDefinition parentBeanDefinition = getParentBeanDefinition(e);
                //TODO change to exact class name
                if (parentBeanDefinition.getBeanClassName().contains("Flow"))
                {
                    if (parentBeanDefinition.isAbstract())
                    {
                        isAbstractFlow = true;
                    }
                    break;
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
