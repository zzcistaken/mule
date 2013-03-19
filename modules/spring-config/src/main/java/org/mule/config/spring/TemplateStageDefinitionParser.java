package org.mule.config.spring;

import org.mule.config.spring.parsers.delegate.ParentContextDefinitionParser;
import org.mule.config.spring.parsers.generic.ChildDefinitionParser;

import org.w3c.dom.Element;

/**
 *
 */
public class TemplateStageDefinitionParser  extends ParentContextDefinitionParser
{

    private static TemplateStageNoUniqueName templateStage = new TemplateStageNoUniqueName("messageProcessor");


    static {
        templateStage.addIgnored("documentation");
    }

    public TemplateStageDefinitionParser()
    {
        super("flow-template", templateStage);
        TemplateStageNoUniqueName templateStage = new TemplateStageNoUniqueName("templateStage");
        otherwise(templateStage);
    }

    public static class TemplateStageNoUniqueName extends ChildDefinitionParser
    {

        public TemplateStageNoUniqueName(String setterMethod)
        {
            super(setterMethod,TemplateStage.class);
        }

        @Override
        protected void checkElementNameUnique(Element element)
        {
            return;
        }
    }
}
