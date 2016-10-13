/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.deployment.internal;

import org.mule.runtime.module.artifact.classloader.ArtifactClassLoaderManager;
import org.mule.runtime.module.artifact.classloader.ArtifactClassLoader;
import org.mule.runtime.module.artifact.classloader.ArtifactClassLoaderRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultArtifactClassLoaderManager implements ArtifactClassLoaderManager, ArtifactClassLoaderRepository {

  private final Map<String, ArtifactClassLoader> artifactClassLoaders = new ConcurrentHashMap<>();

  @Override
  public void add(ArtifactClassLoader artifactClassLoader) {

    artifactClassLoaders.put(artifactClassLoader.getArtifactId(), artifactClassLoader);
  }

  @Override
  public ArtifactClassLoader remove(String artifactId) {
    return artifactClassLoaders.remove(artifactId);
  }

  @Override
  public ClassLoader find(String artifactId) {

    ClassLoader result = null;

    final ArtifactClassLoader artifactClassLoader = artifactClassLoaders.get(artifactId);
    if (artifactClassLoader != null) {
      result = artifactClassLoader.getClassLoader();
    }

    return result;
  }
}
