/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.runner.api;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.io.FileUtils.toFile;
import static org.apache.commons.lang.StringUtils.endsWithIgnoreCase;
import static org.eclipse.aether.util.artifact.JavaScopes.COMPILE;
import static org.eclipse.aether.util.artifact.JavaScopes.PROVIDED;
import static org.eclipse.aether.util.filter.DependencyFilterUtils.orFilter;
import static org.mule.test.runner.api.ArtifactClassificationType.MODULE;
import static org.mule.test.runner.utils.FileUtils.toUrl;
import org.mule.test.runner.classification.PatternExclusionsDependencyFilter;
import org.mule.test.runner.classification.PatternInclusionsDependencyFilter;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactResolutionException;

/**
 * TODO
 */
public class ContainerClassifier extends AbstractArtifactClassifier<ArtifactClassification> {

  private static final String POM = "pom";
  private static final String POM_XML = POM + ".xml";
  private static final String POM_EXTENSION = "." + POM;
  private static final String ZIP_EXTENSION = ".zip";

  private static final String JAR_EXTENSION = "jar";
  private static final String SNAPSHOT_WILCARD_FILE_FILTER = "*-SNAPSHOT*.*";
  private static final String TESTS_JAR = "-tests.jar";

  @Override
  public List<ArtifactClassification> classify(DependencyResolver dependencyResolver, ClassPathClassifierContext context,
                                               List<Dependency> directDependencies, Artifact rootArtifact,
                                               ArtifactClassificationType rootArtifactType) {
    directDependencies = directDependencies.stream()
        .filter(getContainerDirectDependenciesFilter(rootArtifactType))
        .map(depToTransform -> depToTransform.setScope(COMPILE))
        .collect(toList());

    logger.debug("Selected direct dependencies to be used for resolving container dependency graph (changed to compile in " +
                     "order to resolve the graph): {}", directDependencies);

    Set<Dependency> managedDependencies = selectContainerManagedDependencies(dependencyResolver, context, directDependencies, rootArtifactType);

    logger.debug("Collected managed dependencies from direct provided dependencies to be used for resolving container "
                     + "dependency graph: {}", managedDependencies);

    List<String> excludedFilterPattern = newArrayList(context.getProvidedExclusions());
    excludedFilterPattern.addAll(context.getExcludedArtifacts());
    excludedFilterPattern.addAll(context.getPluginClassifications().stream()
                                     .map(pluginUrlClassification -> pluginUrlClassification.getArtifactId())
                                     .collect(toList()));
    excludedFilterPattern.addAll(context.getServiceClassifications().stream()
                                     .map(serviceUrlClassification -> serviceUrlClassification.getArtifactId())
                                     .collect(toList()));

    logger.debug("Resolving dependencies for container using exclusion filter patterns: {}", excludedFilterPattern);
    if (!context.getProvidedInclusions().isEmpty()) {
      logger.debug("Resolving dependencies for container using inclusion filter patterns: {}", context.getProvidedInclusions());
    }

    final DependencyFilter dependencyFilter = orFilter(new PatternInclusionsDependencyFilter(context.getProvidedInclusions()),
                                                       new PatternExclusionsDependencyFilter(excludedFilterPattern));

    List<URL> containerUrls;
    try {
      containerUrls = toUrl(dependencyResolver.resolveDependencies(null, directDependencies, newArrayList(managedDependencies),
                                                                   dependencyFilter));
    } catch (Exception e) {
      throw new IllegalStateException("Couldn't resolve dependencies for Container", e);
    }
    containerUrls = containerUrls.stream().filter(url -> {
      String file = toFile(url).getAbsolutePath();
      return !(endsWithIgnoreCase(file, POM_XML) || endsWithIgnoreCase(file, POM_EXTENSION) || endsWithIgnoreCase(file,
                                                                                                                  ZIP_EXTENSION));
    }).collect(toList());

    if (MODULE.equals(rootArtifactType)) {
      File rootArtifactOutputFile = resolveRootArtifactFile(dependencyResolver, context.getRootArtifact());
      if (rootArtifactOutputFile == null) {
        throw new IllegalStateException("rootArtifact (" + context.getRootArtifact()
                                            + ") identified as MODULE but doesn't have an output");
      }
      containerUrls.add(toUrl(rootArtifactOutputFile));
    }

    //TODO: just another classifier "filter" that checks for snapshots on IDE execution only
    resolveSnapshotVersionsToTimestampedFromClassPath(containerUrls, context.getClassPathURLs());

    return newArrayList(new ArtifactClassification("container", "container", containerUrls));
  }

  /**
   * Eclipse Aether is set to work in {@code offline} mode and to ignore artifact descriptors repositories the metadata for
   * SNAPSHOTs versions cannot be read from remote repositories. So, it will always resolve SNAPSHOT dependencies as normalized,
   * meaning that the resolved URL/File will have the SNAPSHOT format instead of timestamped one.
   * <p/>
   * At the same time IDEs or even Maven when running tests will resolve to timestamped versions instead, so we must do this
   * "resolve" operation that matches SNAPSHOTs resolved artifacts to timestamped SNAPSHOT versions from classpath.
   *
   * @param resolvedURLs {@link URL}s resolved from the dependency graph
   * @param classpathURLs {@link URL}s already provided in class path by IDE or Maven
   */
  private void resolveSnapshotVersionsToTimestampedFromClassPath(List<URL> resolvedURLs, List<URL> classpathURLs) {
    logger.debug("Checking if resolved SNAPSHOT URLs had a timestamped version already included in class path URLs");
    Map<File, List<URL>> classpathFolders = groupArtifactUrlsByFolder(classpathURLs);

    FileFilter snapshotFileFilter = new WildcardFileFilter(SNAPSHOT_WILCARD_FILE_FILTER);
    ListIterator<URL> listIterator = resolvedURLs.listIterator();
    while (listIterator.hasNext()) {
      final URL urlResolved = listIterator.next();
      File artifactResolvedFile = toFile(urlResolved);
      if (snapshotFileFilter.accept(artifactResolvedFile)) {
        File artifactResolvedFileParentFile = artifactResolvedFile.getParentFile();
        logger.debug("Checking if resolved SNAPSHOT artifact: '{}' has a timestamped version already in class path",
                     artifactResolvedFile);
        URL urlFromClassPath = null;
        if (classpathFolders.containsKey(artifactResolvedFileParentFile)) {
          urlFromClassPath = findArtifactUrlFromClassPath(classpathFolders, artifactResolvedFile);
        }

        if (urlFromClassPath != null) {
          logger.debug("Replacing resolved URL '{}' from class path URL '{}'", urlResolved, urlFromClassPath);
          listIterator.set(urlFromClassPath);
        } else {
          logger.error("'{}' resolved SNAPSHOT version couldn't be matched to a class path URL: '{}'", artifactResolvedFile,
                       classpathURLs);
          throw new IllegalStateException(artifactResolvedFile
                                              + " resolved SNAPSHOT version couldn't be matched to a class path URL");
        }
      }
    }
  }

  /**
   * Creates a {@link Map} that has as key the folder that holds the artifact and value a {@link List} of {@link URL}s. For
   * instance, an artifact in class path that only has its jar packaged output:
   *
   * <pre>
   *   key=/Users/jdoe/.m2/repository/org/mule/extensions/mule-extensions-api-xml-dsl/1.0.0-SNAPSHOT/
   *   value=[file:/Users/jdoe/.m2/repository/org/mule/extensions/mule-extensions-api-xml-dsl/1.0.0-SNAPSHOT/mule-extensions-api-xml-dsl-1.0.0-20160823.170911-32.jar]
   * </pre>
   * <p/>
   * Another case is for those artifacts that have both packaged versions, the jar and the -tests.jar. For instance:
   *
   * <pre>
   *   key=/Users/jdoe/Development/mule/extensions/file/target
   *   value=[file:/Users/jdoe/.m2/repository/org/mule/modules/mule-module-file-extension-common/4.0-SNAPSHOT/mule-module-file-extension-common-4.0-SNAPSHOT.jar,
   *          file:/Users/jdoe/.m2/repository/org/mule/modules/mule-module-file-extension-common/4.0-SNAPSHOT/mule-module-file-extension-common-4.0-SNAPSHOT-tests.jar]
   * </pre>
   *
   * @param classpathURLs the class path {@link List} of {@link URL}s to be grouped by folder
   * @return {@link Map} that has as key the folder that holds the artifact and value a {@link List} of {@link URL}s.
   */
  private Map<File, List<URL>> groupArtifactUrlsByFolder(List<URL> classpathURLs) {
    Map<File, List<URL>> classpathFolders = newHashMap();
    classpathURLs.forEach(url -> {
      File folder = toFile(url).getParentFile();
      if (classpathFolders.containsKey(folder)) {
        classpathFolders.get(folder).add(url);
      } else {
        classpathFolders.put(folder, newArrayList(url));
      }
    });
    return classpathFolders;
  }

  /**
   * Finds the corresponding {@link URL} in class path grouped by folder {@link Map} for the given artifact {@link File}.
   *
   * @param classpathFolders a {@link Map} that has as entry the folder of the artifacts from class path and value a {@link List}
   *        with the artifacts (jar, tests.jar, etc).
   * @param artifactResolvedFile the {@link Artifact} resolved from the Maven dependencies and resolved as SNAPSHOT
   * @return {@link URL} for the artifact found in the class path or {@code null}
   */
  private URL findArtifactUrlFromClassPath(Map<File, List<URL>> classpathFolders, File artifactResolvedFile) {
    List<URL> urls = classpathFolders.get(artifactResolvedFile.getParentFile());
    logger.debug("URLs found for '{}' in class path are: {}", artifactResolvedFile, urls);
    if (urls.size() == 1) {
      return urls.get(0);
    }
    // If more than one is found, we have to check for the case of a test-jar...
    Optional<URL> urlOpt;
    if (endsWithIgnoreCase(artifactResolvedFile.getName(), TESTS_JAR)) {
      urlOpt = urls.stream().filter(url -> toFile(url).getAbsolutePath().endsWith(TESTS_JAR)).findFirst();
    } else {
      urlOpt = urls.stream()
          .filter(url -> {
            String filePath = toFile(url).getAbsolutePath();
            return !filePath.endsWith(TESTS_JAR) && filePath.endsWith(JAR_EXTENSION);
          }).findFirst();
    }
    return urlOpt.orElse(null);
  }


  /**
   * Gets the direct dependencies filter to be used when collecting Container dependencies.
   * If the rootArtifact is a {@link ArtifactClassificationType#MODULE} it will include
   * {@value org.eclipse.aether.util.artifact.JavaScopes#COMPILE} dependencies too if not just
   * {@value org.eclipse.aether.util.artifact.JavaScopes#PROVIDED}.
   *
   * @param rootArtifactType the {@link ArtifactClassificationType} for rootArtifact
   * @return {@link Predicate} for selecting direct dependencies for the Container.
   */
  private Predicate<Dependency> getContainerDirectDependenciesFilter(ArtifactClassificationType rootArtifactType) {
    return rootArtifactType.equals(MODULE)
        ? directDep -> directDep.getScope().equals(PROVIDED) || directDep.getScope().equals(COMPILE)
        : directDep -> directDep.getScope().equals(PROVIDED);
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


  /**
   * Creates the {@link Set} of {@link Dependency} to be used as managed dependencies when resolving Container dependencies.
   * If the rootArtifact is a {@link ArtifactClassificationType#MODULE} it will use its managed dependencies, other case it
   * collects managed dependencies for each direct dependencies selected for Container.
   *
   * @param context {@link ClassPathClassifierContext} with settings for the classification process
   * @param directDependencies {@link List} of {@link Dependency} with direct dependencies for the rootArtifact
   * @param rootArtifactType {@link ArtifactClassificationType} for rootArtifact
   * @return {@link Set} of {@link Dependency} to be used as managed dependencies when resolving Container dependencies
   */
  private Set<Dependency> selectContainerManagedDependencies(DependencyResolver dependencyResolver,
                                                             ClassPathClassifierContext context,
                                                             List<Dependency> directDependencies,
                                                             ArtifactClassificationType rootArtifactType) {
    Set<Dependency> managedDependencies;
    if (!rootArtifactType.equals(MODULE)) {
      managedDependencies = directDependencies.stream()
          .map(directDep -> {
            try {
              return dependencyResolver.readArtifactDescriptor(directDep.getArtifact()).getManagedDependencies();
            } catch (ArtifactDescriptorException e) {
              throw new IllegalStateException("Couldn't read artifact: '" + directDep.getArtifact() +
                                                  "' while collecting managed dependencies for Container", e);
            }
          })
          .flatMap(l -> l.stream())
          .collect(toSet());
    } else {
      try {
        managedDependencies = newHashSet(dependencyResolver.readArtifactDescriptor(context.getRootArtifact())
                                             .getManagedDependencies());
      } catch (ArtifactDescriptorException e) {
        throw new IllegalStateException("Couldn't collect managed dependencies for rootArtifact (" + context.getRootArtifact()
                                            + ")", e);
      }
    }
    return managedDependencies;
  }

}
