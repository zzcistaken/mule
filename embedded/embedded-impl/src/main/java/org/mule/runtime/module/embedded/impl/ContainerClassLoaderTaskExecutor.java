/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.embedded.impl;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.deployment.model.api.application.Application;
import org.mule.runtime.deployment.model.api.application.ApplicationDescriptor;
import org.mule.runtime.module.deployment.impl.internal.MuleArtifactResourcesRegistry;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Executor of task that must be run within the container classloader.
 * <p>
 * Since the task may use the new operation to create container instances, and those instances must be created using the container
 * class loader this class must be loaded using the container class loader.
 * 
 * @since 4.0
 */
public class ContainerClassLoaderTaskExecutor {

  private ClassLoader containerClassLoader;
  private Application application;

  /**
   * Creates a new instance.
   * 
   * @param containerClassLoader the container class loader
   */
  public ContainerClassLoaderTaskExecutor(ClassLoader containerClassLoader) {
    this.containerClassLoader = containerClassLoader;
  }

  /**
   * Executes a task within the context of the container class loader.
   * <p>
   * This implies that all new operations executed by the task will be using the container class loader and the
   * {@link Thread#getContextClassLoader()} will be the one from the class loader.
   * 
   * @param task task to be executed.
   */
  private void executeTask(Runnable task) {
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(containerClassLoader);
      task.run();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
  }

  public void createApplicationTask(List<String> configResources, File applicationFolder, File domainFolder) {
    executeTask(() -> {
      try {
        MuleArtifactResourcesRegistry artifactResourcesRegistry = new MuleArtifactResourcesRegistry.Builder().build();

        try {
          artifactResourcesRegistry.getServiceManager().start();
          artifactResourcesRegistry.getExtensionModelLoaderManager().start();
        } catch (MuleException e) {
          throw new IllegalStateException(e);
        }

        ApplicationDescriptor applicationDescriptor =
            artifactResourcesRegistry.getApplicationDescriptorFactory().create(applicationFolder);
        applicationDescriptor.setConfigResources(configResources);
        applicationDescriptor.setAbsoluteResourcePaths(configResources.toArray(new String[0]));

        artifactResourcesRegistry.getDomainFactory().createArtifact(domainFolder);

        this.application = artifactResourcesRegistry.getApplicationFactory().createAppFrom(applicationDescriptor);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }
}
