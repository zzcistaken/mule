/*
 * $Id: NamespaceHandler.vm 10621 2008-01-30 12:15:16Z dirk.olmes $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.activiti.config;

import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.endpoint.URIBuilder;
import org.mule.transport.activiti.ActivitiConnector;
import org.mule.transport.activiti.action.CreateProcessAction;
import org.mule.transport.activiti.action.ListAssignedTasksAction;
import org.mule.transport.activiti.action.ListCandidateGroupTasksAction;
import org.mule.transport.activiti.action.ListCandidateTasksAction;
import org.mule.transport.activiti.action.ListProcessDefinitionsAction;
import org.mule.transport.activiti.action.PerformTaskOperationAction;

/**
 * Registers a Bean Definition Parser for handling <code><activiti:connector></code> elements
 * and supporting endpoint elements.
 */
public class ActivitiNamespaceHandler extends AbstractMuleNamespaceHandler
{
    private static final String ACTIVITI_SERVER = "activiti-server";

    public void init()
    {
        registerStandardTransportEndpoints(ActivitiConnector.ACTIVITI, new String[]{ACTIVITI_SERVER}).addAlias(ACTIVITI_SERVER, URIBuilder.PATH);
        registerConnectorDefinitionParser(ActivitiConnector.class);
        
        //INBOUND ACTIONS
        registerBeanDefinitionParser("list-process-definitions", new ActionChildDefinitionParser("action", ListProcessDefinitionsAction.class));
        registerBeanDefinitionParser("list-assigned-tasks", new ActionChildDefinitionParser("action", ListAssignedTasksAction.class));
        registerBeanDefinitionParser("list-candidate-tasks", new ActionChildDefinitionParser("action", ListCandidateTasksAction.class));
        registerBeanDefinitionParser("list-candidate-group-tasks", new ActionChildDefinitionParser("action", ListCandidateGroupTasksAction.class));
        
        //OUTBOUND ACTIONS
        registerBeanDefinitionParser("create-process", new ActionChildDefinitionParser("action", CreateProcessAction.class));
        registerBeanDefinitionParser("perform-task-operation", new ActionChildDefinitionParser("action", PerformTaskOperationAction.class));
    }
}
