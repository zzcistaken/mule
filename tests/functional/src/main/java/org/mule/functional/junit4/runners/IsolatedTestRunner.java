/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.functional.junit4.runners;

import static org.mule.functional.junit4.runners.ClassLoaderPerTestRunner.createIsolatedClassLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.internal.builders.AnnotatedBuilder;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class IsolatedTestRunner extends Runner implements Filterable
{

    private final Runner delegate;
    private final ClassLoader isolatedClassLoader;

    public IsolatedTestRunner(Class<?> clazz, RunnerBuilder builder) throws Exception
    {
        isolatedClassLoader = createIsolatedClassLoader(clazz);

        final Class<?> isolatedTestClass = getTestClass(clazz);

        final Class<? extends Annotation> runnerDelegateToClass = (Class<? extends Annotation>) isolatedClassLoader.loadClass(RunnerDelegateTo.class.getName());
        final Annotation runnerDelegateToAnnotation = isolatedTestClass.getAnnotation(runnerDelegateToClass);
        if (runnerDelegateToAnnotation != null)
        {
            final Method valueMethod = runnerDelegateToClass.getMethod("value");
            final AnnotatedBuilder annotatedBuilder = new AnnotatedBuilder(builder);
            delegate = annotatedBuilder.buildRunner((Class<? extends Runner>) valueMethod.invoke(runnerDelegateToAnnotation), isolatedTestClass);
        }
        else
        {
             delegate = new BlockJUnit4ClassRunner(isolatedTestClass);
        }
    }

    private Class<?> getTestClass(Class<?> clazz) throws InitializationError
    {
        try
        {
            return isolatedClassLoader.loadClass(clazz.getName());
        }
        catch (Exception e)
        {
            throw new InitializationError(e);
        }
    }

    @Override
    public Description getDescription()
    {
        return delegate.getDescription();
    }

    @Override
    public void run(RunNotifier notifier)
    {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(isolatedClassLoader);
            delegate.run(notifier);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException
    {
        if (delegate instanceof Filterable)
        {
            ((Filterable) delegate).filter(filter);
        }
    }
}
