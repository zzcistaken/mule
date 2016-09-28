/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.deployment.model.internal;

import static java.util.Collections.emptySet;
import static org.mule.runtime.container.api.MuleFoldersUtil.getAppClassesFolder;
import org.mule.runtime.deployment.model.api.DeployableArtifactDescriptor;
import org.mule.runtime.module.artifact.classloader.ArtifactClassLoaderFilter;
import org.mule.runtime.module.artifact.classloader.DefaultArtifactClassLoaderFilter;
import org.mule.runtime.module.artifact.util.FileJarExplorer;
import org.mule.runtime.module.artifact.util.JarExplorer;
import org.mule.runtime.module.artifact.util.JarInfo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

//TODO(pablo.kraan): ths class name is wrong and collides with ArtifactClassLoaderFilterFactory
public class ArtifactClassloaderFilterFactory {

  public ArtifactClassLoaderFilter create(DeployableArtifactDescriptor descriptor) {
    final JarInfo librariesInfo = findExportedResources(descriptor.getSharedRuntimeLibs());
    final JarInfo classesInfo;
    try {
      final File appClassesFolder = getAppClassesFolder(descriptor.getName());
      if (appClassesFolder.exists()) {
        classesInfo = findExportedResources(appClassesFolder.toURI().toURL());
      } else {
        classesInfo = new JarInfo(emptySet(), emptySet());
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Cannot read application classes folder", e);
    }
    librariesInfo.getPackages().addAll(classesInfo.getPackages());
    librariesInfo.getResources().addAll(classesInfo.getResources());

    return new DefaultArtifactClassLoaderFilter(librariesInfo.getPackages(), librariesInfo.getResources());
  }

  private JarInfo findExportedResources(URL... libraries) {
    Set<String> packages = new HashSet<>();
    Set<String> resources = new HashSet<>();
    final JarExplorer jarExplorer = new FileJarExplorer();

    for (URL library : libraries) {
      final JarInfo jarInfo = jarExplorer.explore(library);
      packages.addAll(jarInfo.getPackages());
      resources.addAll(jarInfo.getResources());
    }

    return new JarInfo(packages, resources);
  }
}

