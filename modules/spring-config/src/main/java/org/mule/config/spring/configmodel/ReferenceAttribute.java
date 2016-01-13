package org.mule.config.spring.configmodel;

/**
 * Created by pablolagreca on 1/12/16.
 */
public class ReferenceAttribute extends Attribute {

    private ComponentDefinitionModel reference;

    public ComponentDefinitionModel getReference()
    {
        return this.reference;
    }

}
