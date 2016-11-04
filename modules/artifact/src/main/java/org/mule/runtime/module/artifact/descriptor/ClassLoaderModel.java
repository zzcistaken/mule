/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.artifact.descriptor;

import static java.util.Collections.emptySet;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassLoaderModel {

  public static final ClassLoaderModel NULL_CLASSLOADER_MODEL =
      new ClassLoaderModel(new URL[0], emptySet(), emptySet(), emptySet());

  private final URL[] urls;
  private final Set<String> exportedPackages;
  private final Set<String> exportedResources;
  private final Set<String> dependencies;


  private ClassLoaderModel(URL[] urls, Set<String> exportedPackages, Set<String> exportedResources, Set<String> dependencies) {
    this.urls = urls;
    this.exportedPackages = exportedPackages;
    this.exportedResources = exportedResources;
    this.dependencies = dependencies;
  }


  public URL[] getUrls() {
    return urls;
  }

  public Set<String> getExportedPackages() {
    return exportedPackages;
  }

  public Set<String> getExportedResources() {
    return exportedResources;
  }

  public Set<String> getDependencies() {
    return dependencies;
  }

  public static class ClassLoaderModelBuilder {

    private Set<String> packages = new HashSet<>();
    private Set<String> resources = new HashSet<>();
    private List<URL> urls = new ArrayList<>();
    private Set<String> dependencies = new HashSet<>();

    public ClassLoaderModelBuilder() {}

    public ClassLoaderModelBuilder(ClassLoaderModel source) {
      //TODO(pablo.kraan): model - make a safe copy
      this.packages = source.exportedPackages;
      this.resources = source.exportedResources;
      this.urls = Arrays.asList(source.urls);
      this.dependencies = source.dependencies;
    }

    public ClassLoaderModelBuilder exportingPackages(Set<String> packages) {
      this.packages = packages;
      return this;
    }

    public ClassLoaderModelBuilder exportingResources(Set<String> packages) {
      this.resources = packages;
      return this;
    }

    public ClassLoaderModelBuilder dependingOn(Set<String> dependencies) {
      this.dependencies = dependencies;
      return this;
    }

    public ClassLoaderModelBuilder containing(URL url) {
      urls.add(url);
      return this;
    }

    public ClassLoaderModel build() {
      return new ClassLoaderModel(urls.toArray(new URL[0]), packages, resources, dependencies);
    }
  }


}
