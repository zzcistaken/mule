package org.mule.config.spring.configmodel;

public interface Type
{

    boolean isCollection();

    boolean isMap();

    boolean isSimpleType();

    Class<?> getType();

}
