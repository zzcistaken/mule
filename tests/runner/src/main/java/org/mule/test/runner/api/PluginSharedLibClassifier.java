/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.runner.api;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.eclipse.aether.util.artifact.JavaScopes.TEST;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;

/**
 * Classification for plugin shared libs.
 *
 * @since 4.0
 */
public class PluginSharedLibClassifier extends AbstractArtifactClassifier<ArtifactUrlClassification> {

  /**
   * Classifies the {@link List} of {@link URL}s from {@value org.eclipse.aether.util.artifact.JavaScopes#TEST} scope direct
   * dependencies to be added as plugin runtime shared libraries.
   *
   * @param dependencyResolver {@link DependencyResolver} to resolve dependencies
   * @param context {@link ClassPathClassifierContext} with settings for the classification process
   * @param directDependencies {@link List} of {@link Dependency} with direct dependencies for the rootArtifact
   * @param rootArtifact {@link ArtifactClassificationType} for rootArtifact
   * @param rootArtifactType {@link ArtifactClassificationType} defines the type of root artifact
   * @return {@link ArtifactUrlClassification} with list of {@link URL}s to be added to runtime shared libraries.
   */
  @Override
  public List<ArtifactUrlClassification> classify(DependencyResolver dependencyResolver, ClassPathClassifierContext context,
                                                  List<Dependency> directDependencies,
                                                  Artifact rootArtifact, ArtifactClassificationType rootArtifactType) {
    List<URL> pluginSharedLibUrls = newArrayList();

    List<Dependency> pluginSharedLibDependencies = context.getSharedPluginLibCoordinates().stream()
        .map(sharedPluginLibCoords -> findPluginSharedLibArtifact(sharedPluginLibCoords, context.getRootArtifact(),
                                                                  directDependencies))
        .collect(toList());

    logger.debug("Plugin sharedLib artifacts matched with versions from direct dependencies declared: {}",
                 pluginSharedLibDependencies);

    pluginSharedLibDependencies.stream()
        .map(pluginSharedLibDependency -> {
          try {
            return dependencyResolver.resolveArtifact(pluginSharedLibDependency.getArtifact())
                .getArtifact().getFile().toURI().toURL();
          } catch (Exception e) {
            throw new IllegalStateException("Error while resolving dependency '" + pluginSharedLibDependency
                + "' as plugin sharedLibs");
          }
        })
        .forEach(pluginSharedLibUrls::add);

    logger.debug("Classified URLs as plugin runtime shared libraries: '{}", pluginSharedLibUrls);

    //TODO: refactor this to avoid having artifactId on top of the hierarchy
    return newArrayList(new ArtifactUrlClassification("pluginSharedLib", "pluginSharedLib", pluginSharedLibUrls));
  }

  /**
   * Finds the plugin shared lib {@link Dependency} from the direct dependencies of the  rootArtifact.
   *
   * @param pluginSharedLibCoords Maven coordinates that define the plugin shared lib artifact
   * @param rootArtifact {@link Artifact} that defines the current artifact that requested to build this class loaders
   * @param directDependencies {@link List} of {@link Dependency} with direct dependencies for the rootArtifact
   * @return {@link Artifact} representing the plugin shared lib artifact
   */
  private Dependency findPluginSharedLibArtifact(String pluginSharedLibCoords, Artifact rootArtifact,
                                                   List<Dependency> directDependencies) {
    Optional<Dependency> pluginSharedLibDependency = discoverDependency(pluginSharedLibCoords, rootArtifact, directDependencies);
    if (!pluginSharedLibDependency.isPresent() || !pluginSharedLibDependency.get().getScope().equals(TEST)) {
      throw new IllegalStateException("Plugin shared lib artifact '" + pluginSharedLibCoords +
                                          "' in order to be resolved has to be declared as " + TEST + " dependency of your Maven project (" + rootArtifact + ")");
    }

    return pluginSharedLibDependency.get();
  }

}
