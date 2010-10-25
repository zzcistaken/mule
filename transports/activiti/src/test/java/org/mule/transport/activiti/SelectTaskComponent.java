/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.activiti;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import org.mule.transport.activiti.action.Operation;
import org.mule.transport.activiti.action.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectTaskComponent implements Callable
{

    public Object onCall(MuleEventContext eventContext) throws Exception
    {
        List<Task> tasks = (List<Task>) eventContext.getMessage().getPayload();
        Task task = tasks.get(0);
        Map map = new HashMap();
        map.put("taskId", task.getId());
        map.put("operation", Operation.CLAIM);
        return map;
    }

}


