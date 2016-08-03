/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.lifecycle;

import static java.util.Arrays.asList;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.context.notification.ServerNotification;
import org.mule.runtime.core.util.ClassUtils;

import java.util.HashSet;
import java.util.Set;

public class LifecycleObject
{
    
    private Class type;
    private Set<Class<?>> hierarchyExclusion = new HashSet<>();
    private ServerNotification preNotification;
    private ServerNotification postNotification;

    public LifecycleObject(Class type)
    {
        this(type, null);
    }

    public LifecycleObject(Class type, Class<?>... hierarchyExclusion)
    {
        this.type = type;
        if (hierarchyExclusion != null) {
            this.hierarchyExclusion.addAll(asList(hierarchyExclusion));
        }
    }

    public ServerNotification getPostNotification()
    {
        return postNotification;
    }

    public void setPostNotification(ServerNotification postNotification)
    {
        this.postNotification = postNotification;
    }

    public ServerNotification getPreNotification()
    {
        return preNotification;
    }

    public void setPreNotification(ServerNotification preNotification)
    {
        this.preNotification = preNotification;
    }

    public Class getType()
    {
        return type;
    }

    public Set<Class<?>> getHierarchyExclusion()
    {
        return hierarchyExclusion;
    }

    public void setType(Class type)
    {
        this.type = type;
    }

    public void firePreNotification(MuleContext context)
    {
        if(preNotification!=null)
        {
            context.fireNotification(preNotification);
        }
    }

    public void firePostNotification(MuleContext context)
    {
        if(postNotification!=null)
        {
            context.fireNotification(postNotification);
        }
    }

    @Override
    public String toString()
    {
        return super.toString() + " (" + ClassUtils.getSimpleName(type) + ")";
    }
        
}
