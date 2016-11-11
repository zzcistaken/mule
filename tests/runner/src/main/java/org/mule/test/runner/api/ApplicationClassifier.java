/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.runner.api;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.eclipse.aether.util.artifact.JavaScopes.COMPILE;
import static org.eclipse.aether.util.artifact.JavaScopes.PROVIDED;
import static org.eclipse.aether.util.artifact.JavaScopes.TEST;
import static org.eclipse.aether.util.filter.DependencyFilterUtils.orFilter;
import static org.mule.test.runner.api.ArtifactClassificationType.APPLICATION;
import static org.mule.test.runner.api.ArtifactClassificationType.PLUGIN;
import static org.mule.test.runner.utils.FileUtils.toUrl;
import org.mule.test.runner.classification.PatternExclusionsDependencyFilter;
import org.mule.test.runner.classification.PatternInclusionsDependencyFilter;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.resolution.ArtifactResolutionException;

/**
 * TODO
 */
public class ApplicationClassifier extends AbstractArtifactClassifier<ArtifactClassification> {

  private static final String JAR_EXTENSION = "jar";
  private static final String MAVEN_COORDINATES_SEPARATOR = ":";
  private static final String TESTS_CLASSIFIER = "tests";

  @Override
  public List<ArtifactClassification> classify(DependencyResolver dependencyResolver, ClassPathClassifierContext context,
                                               List<Dependency> directDependencies, Artifact rootArtifact,
                                               ArtifactClassificationType rootArtifactType) {
    logger.debug("Building application classification");

    DependencyFilter dependencyFilter = new PatternInclusionsDependencyFilter(context.getTestInclusions());
    logger.debug("Using filter for dependency graph to include: '{}'", context.getTestInclusions());

    List<File> appFiles = newArrayList();
    List<String> exclusionsPatterns = newArrayList();

    if (APPLICATION.equals(rootArtifactType)) {
      logger.debug("RootArtifact identified as {} so is going to be added to application classification", APPLICATION);
      File rootArtifactOutputFile = resolveRootArtifactFile(dependencyResolver, rootArtifact);
      if (rootArtifactOutputFile != null) {
        appFiles.add(rootArtifactOutputFile);
      } else {
        logger.warn("rootArtifact '{}' identified as {} but doesn't have an output {} file", rootArtifact, rootArtifactType,
                    JAR_EXTENSION);
      }
    } else {
      logger.debug("RootArtifact already classified as plugin or module, excluding it from application classification");
      exclusionsPatterns.add(rootArtifact.getGroupId() + MAVEN_COORDINATES_SEPARATOR + rootArtifact.getArtifactId() +
                                 MAVEN_COORDINATES_SEPARATOR + "*" + MAVEN_COORDINATES_SEPARATOR + rootArtifact.getVersion());
    }

    directDependencies = directDependencies.stream()
        .map(toTransform -> {
          if (toTransform.getScope().equals(TEST)) {
            return new Dependency(toTransform.getArtifact(), COMPILE);
          }
          if (PLUGIN.equals(rootArtifactType) && toTransform.getScope().equals(COMPILE)) {
            return new Dependency(toTransform.getArtifact(), PROVIDED);
          }
          return toTransform;
        })
        .collect(toList());

    logger.debug("OR exclude: {}", context.getExcludedArtifacts());
    exclusionsPatterns.addAll(context.getExcludedArtifacts());

    if (!context.getTestExclusions().isEmpty()) {
      logger.debug("OR exclude application specific artifacts: {}", context.getTestExclusions());
      exclusionsPatterns.addAll(context.getTestExclusions());
    }

    try {
      List<Dependency> managedDependencies =
          newArrayList(dependencyResolver.readArtifactDescriptor(rootArtifact).getManagedDependencies());
      managedDependencies.addAll(directDependencies.stream()
                                     .filter(directDependency -> !directDependency.getScope().equals(TEST))
                                     .collect(toList()));
      logger.debug("Resolving dependency graph for '{}' scope direct dependencies: {} and managed dependencies {}",
                   TEST, directDependencies, managedDependencies);

      final Dependency rootTestDependency = new Dependency(new DefaultArtifact(rootArtifact.getGroupId(),
                                                                               rootArtifact.getArtifactId(), TESTS_CLASSIFIER,
                                                                               JAR_EXTENSION,
                                                                               rootArtifact.getVersion()),
                                                           TEST);

      appFiles
          .addAll(dependencyResolver
                      .resolveDependencies(rootTestDependency, directDependencies, managedDependencies,
                                           orFilter(dependencyFilter,
                                                    new PatternExclusionsDependencyFilter(exclusionsPatterns))));
    } catch (Exception e) {
      throw new IllegalStateException("Couldn't resolve dependencies for application '" + context.getRootArtifact()
                                          + "' classification", e);
    }

    List<URL> appUrls = newArrayList(toUrl(appFiles));
    logger.debug("Appending URLs to application: {}", context.getApplicationUrls());
    appUrls.addAll(context.getApplicationUrls());
    return newArrayList(new ArtifactClassification(toClassifierLessId(rootArtifact), rootArtifact.getArtifactId(), appUrls));
  }

  /**
   * Resolves the rootArtifact {@value #JAR_EXTENSION} output {@link File}s to be added to class loader.
   *
   * @param rootArtifact {@link Artifact} being classified
   * @return {@link File} to be added to class loader
   */
  private File resolveRootArtifactFile(DependencyResolver dependencyResolver, Artifact rootArtifact) {
    final DefaultArtifact jarArtifact = new DefaultArtifact(rootArtifact.getGroupId(), rootArtifact.getArtifactId(),
                                                            JAR_EXTENSION, JAR_EXTENSION, rootArtifact.getVersion());
    try {
      return dependencyResolver.resolveArtifact(jarArtifact).getArtifact().getFile();
    } catch (ArtifactResolutionException e) {
      logger.warn("'{}' rootArtifact output {} file couldn't be resolved", rootArtifact, JAR_EXTENSION);
      return null;
    }
  }

}
