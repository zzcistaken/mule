package org.mule.construct;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class TemplateConfiguration
{

    private List<TemplateRedefinableAttribute> templateRedefinableAttributes = Collections.emptyList();

    public List<TemplateRedefinableAttribute> getTemplateRedefinableAttributes()
    {
        return templateRedefinableAttributes;
    }

    public void setTemplateRedefinableAttributes(List<TemplateRedefinableAttribute> templateRedefinableAttributes)
    {
        this.templateRedefinableAttributes = templateRedefinableAttributes;
    }
}
