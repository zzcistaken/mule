/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.executor;

import static org.apache.commons.lang.ArrayUtils.isEmpty;
import static org.mule.api.lifecycle.LifecycleUtils.disposeIfNeeded;
import static org.mule.api.lifecycle.LifecycleUtils.initialiseIfNeeded;
import static org.mule.api.lifecycle.LifecycleUtils.startIfNeeded;
import static org.mule.api.lifecycle.LifecycleUtils.stopIfNeeded;
import static org.springframework.util.ReflectionUtils.invokeMethod;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Lifecycle;
import org.mule.extension.api.runtime.OperationContext;
import org.mule.extension.api.runtime.OperationExecutor;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link OperationExecutor} which relies on a
 * {@link #executorDelegate} and a reference to one of its {@link Method}s.
 * When {@link #execute(OperationContext)} is invoked, the {@link #operationMethod}
 * is invoked over the {@link #executorDelegate}.
 * <p/>
 * All the {@link Lifecycle} events that {@code this} instance receives are propagated
 * to the {@link #executorDelegate}
 *
 * @since 3.7.0
 */
public final class ReflectiveMethodOperationExecutor implements OperationExecutor, MuleContextAware, Lifecycle
{

    private static class NoArgumentsResolverDelegate implements ArgumentResolverDelegate
    {

        private static final Object[] EMPTY = new Object[] {};

        @Override
        public Object[] resolve(OperationContext operationContext)
        {
            return EMPTY;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectiveMethodOperationExecutor.class);
    private static final ArgumentResolverDelegate NO_ARGS_DELEGATE = new NoArgumentsResolverDelegate();

    private final Method operationMethod;
    private final Object executorDelegate;
    private final ArgumentResolverDelegate argumentResolverDelegate;

    private MuleContext muleContext;

    ReflectiveMethodOperationExecutor(Method operationMethod, Object executorDelegate)
    {
        this.operationMethod = operationMethod;
        this.executorDelegate = executorDelegate;
        argumentResolverDelegate = isEmpty(operationMethod.getParameterTypes()) ? NO_ARGS_DELEGATE : new MethodArgumentResolverDelegate(operationMethod);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(OperationContext operationContext) throws Exception
    {
        return invokeMethod(operationMethod, executorDelegate, resolvePrimitiveTypes(operationMethod, getParameterValues(operationContext)));
    }

    private Object[] resolvePrimitiveTypes(Method operationMethod, Object[] parameterValues)
    {
        Object[] resolvedParameters = new Object[parameterValues.length];
        for (int i = 0; i < parameterValues.length; i++)
        {
            Object parameterValue = parameterValues[i];
            if (parameterValue == null)
            {
                resolvedParameters[i] = resolvePrimitiveTypeDefaultValue(operationMethod.getParameterTypes()[i]);
            }
            else
            {
                resolvedParameters[i] = parameterValue;
            }
        }
        return resolvedParameters;
    }

    private Object resolvePrimitiveTypeDefaultValue(Class<?> type)
    {
        if (type.equals(byte.class))
        {
            return (byte) 0;
        }
        if (type.equals(short.class))
        {
            return (short) 0;
        }
        if (type.equals(int.class))
        {
            return 0;
        }
        if (type.equals(long.class))
        {
            return 0l;
        }
        if (type.equals(float.class))
        {
            return 0.0f;
        }
        if (type.equals(double.class))
        {
            return 0.0d;
        }
        if (type.equals(boolean.class))
        {
            return false;
        }
        if (type.equals(char.class))
        {
            return '\u0000';
        }
        return null;
    }

    private Object[] getParameterValues(OperationContext operationContext)
    {
        return argumentResolverDelegate.resolve(operationContext);
    }


    @Override
    public void initialise() throws InitialisationException
    {
        initialiseIfNeeded(executorDelegate, true, muleContext);
    }

    @Override
    public void start() throws MuleException
    {
        startIfNeeded(executorDelegate);
    }

    @Override
    public void stop() throws MuleException
    {
        stopIfNeeded(executorDelegate);
    }

    @Override
    public void dispose()
    {
        disposeIfNeeded(executorDelegate, LOGGER);
    }

    @Override
    public void setMuleContext(MuleContext context)
    {
        muleContext = context;
        if (executorDelegate instanceof MuleContextAware)
        {
            ((MuleContextAware) executorDelegate).setMuleContext(context);
        }
    }
}
