/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.firstextension;

import org.mule.extension.annotation.api.Parameter;

import java.util.List;

/**
 * Created by pablocabrera on 11/25/15.
 */
public class InnerPojo
{

    @Parameter
    private String childName;

    @Parameter
    private Integer age;

    @Parameter
    private List<String> friends;

    public Integer getAge()
    {
        return age;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public String getChildName()
    {
        return childName;
    }

    public void setChildName(String childName)
    {
        this.childName = childName;
    }

    public List<String> getFriends()
    {
        return friends;
    }

    public void setFriends(List<String> friends)
    {
        this.friends = friends;
    }
}
