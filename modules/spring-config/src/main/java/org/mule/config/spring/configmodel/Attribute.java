package org.mule.config.spring.configmodel;

import java.io.Serializable;

public abstract class Attribute implements Serializable {

    private String name;
    private Type type;

    public String getName()
    {
        return this.name;
    }

    public Type getType()
    {
        return this.type;
    }

    public abstract Object getResolvedValue();

}
