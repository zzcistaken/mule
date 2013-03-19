package org.mule.config.spring;

import org.springframework.beans.factory.config.RuntimeBeanReference;

/**
 *
 */
public class MuleRuntimeBeanReference extends RuntimeBeanReference
{

    private final String templateKey;

    public MuleRuntimeBeanReference(String beanName, boolean toParent, String templateKey)
    {
        super(beanName, toParent);
        this.templateKey = templateKey;
    }

    public String getTemplateKey()
    {
        return templateKey;
    }


}
