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
import org.mule.transport.activiti.action.model.ProcessDefinition;

import java.util.List;

public class SelectProcessDefinitionComponent implements Callable
{

    public Object onCall(MuleEventContext eventContext) throws Exception
    {
        List<ProcessDefinition> definitions = (List<ProcessDefinition>) eventContext.getMessage()
            .getPayload();
        ProcessDefinition definition = definitions.get(0);
        return definition.getId();
    }

}
