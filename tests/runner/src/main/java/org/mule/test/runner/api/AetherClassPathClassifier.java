/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.runner.api;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.io.FileUtils.toFile;
import static org.apache.commons.lang.StringUtils.endsWithIgnoreCase;
import static org.eclipse.aether.util.artifact.ArtifactIdUtils.toId;
import static org.eclipse.aether.util.artifact.JavaScopes.COMPILE;
import static org.eclipse.aether.util.artifact.JavaScopes.PROVIDED;
import static org.eclipse.aether.util.artifact.JavaScopes.TEST;
import static org.eclipse.aether.util.filter.DependencyFilterUtils.classpathFilter;
import static org.eclipse.aether.util.filter.DependencyFilterUtils.orFilter;
import static org.mule.runtime.api.util.Preconditions.checkNotNull;
import static org.mule.runtime.core.util.PropertiesUtils.loadProperties;
import static org.mule.runtime.deployment.model.api.plugin.ArtifactPluginDescriptor.MULE_PLUGIN_CLASSIFIER;
import static org.mule.test.runner.api.ArtifactClassificationType.APPLICATION;
import static org.mule.test.runner.api.ArtifactClassificationType.MODULE;
import static org.mule.test.runner.api.ArtifactClassificationType.PLUGIN;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.module.artifact.classloader.ArtifactClassLoader;
import org.mule.runtime.module.extension.internal.manager.DefaultExtensionManager;
import org.mule.test.runner.classification.PatternExclusionsDependencyFilter;
import org.mule.test.runner.classification.PatternInclusionsDependencyFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates the {@link ArtifactUrlClassification} based on the Maven dependencies declared by the rootArtifact using Eclipse
 * Aether. Uses a {@link DependencyResolver} to resolve Maven dependencies.
 * <p/>
 * The classification process classifies the rootArtifact dependencies in three groups: {@code provided}, {@code compile} and
 * {@code test} scopes. It resolves dependencies graph for each group applying filters and exclusions and classifies the list of
 * {@link URL}s that would define each class loader container, plugins and application.
 * <p/>
 * Dependencies resolution uses dependencies management declared by these artifacts while resolving the dependency graph.
 * <p/>
 * Plugins are discovered as {@link Extension} if they do have a annotated a {@link Class}. It generates the {@link Extension}
 * metadata in order to later register it to an {@link org.mule.runtime.extension.api.ExtensionManager}.
 *
 * @since 4.0
 */
public class AetherClassPathClassifier implements ClassPathClassifier {

  private static final String POM = "pom";
  private static final String POM_XML = POM + ".xml";
  private static final String POM_EXTENSION = "." + POM;
  private static final String ZIP_EXTENSION = ".zip";

  private static final String MAVEN_COORDINATES_SEPARATOR = ":";
  private static final String JAR_EXTENSION = "jar";
  private static final String SNAPSHOT_WILCARD_FILE_FILTER = "*-SNAPSHOT*.*";
  private static final String TESTS_CLASSIFIER = "tests";
  private static final String TESTS_JAR = "-tests.jar";
  private static final String SERVICE_PROPERTIES_FILE_NAME = "service.properties";
  private static final String SERVICE_PROVIDER_CLASS_NAME = "service.className";
  private static final String MULE_SERVICE_CLASSIFIER = "mule-service";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private DependencyResolver dependencyResolver;
  private ArtifactClassificationTypeResolver artifactClassificationTypeResolver;
  private DefaultExtensionManager extensionManager = new DefaultExtensionManager();
  private PluginResourcesResolver pluginResourcesResolver = new PluginResourcesResolver(extensionManager);

  /**
   * Creates an instance of the classifier.
   *
   * @param dependencyResolver                 {@link DependencyResolver} to resolve dependencies. Non null.
   * @param artifactClassificationTypeResolver {@link ArtifactClassificationTypeResolver} to identify rootArtifact type. Non null.
   */
  public AetherClassPathClassifier(DependencyResolver dependencyResolver,
                                   ArtifactClassificationTypeResolver artifactClassificationTypeResolver) {
    checkNotNull(dependencyResolver, "dependencyResolver cannot be null");
    checkNotNull(artifactClassificationTypeResolver, "artifactClassificationTypeResolver cannot be null");

    this.dependencyResolver = dependencyResolver;
    this.artifactClassificationTypeResolver = artifactClassificationTypeResolver;
  }

  /**
   * Classifies {@link URL}s and {@link Dependency}s to define how the container, plugins and application class loaders should be
   * created.
   *
   * @param context {@link ClassPathClassifierContext} to be used during the classification. Non null.
   * @return {@link ArtifactsUrlClassification} as result with the classification
   */
  @Override
  public ArtifactsUrlClassification classify(final ClassPathClassifierContext context) {
    checkNotNull(context, "context cannot be null");

    logger.debug("Building class loaders for rootArtifact: {}", context.getRootArtifact());

    List<Dependency> directDependencies;
    try {
      directDependencies = dependencyResolver.getDirectDependencies(context.getRootArtifact());
    } catch (Exception e) {
      throw new IllegalStateException("Couldn't get direct dependencies for rootArtifact: '" + context.getRootArtifact() + "'",
                                      e);
    }

    ArtifactClassificationType rootArtifactType = artifactClassificationTypeResolver
        .resolveArtifactClassificationType(context.getRootArtifact());
    if (rootArtifactType == null) {
      throw new IllegalStateException("Couldn't be identified type for rootArtifact: " + context.getRootArtifact());
    }
    logger.debug("rootArtifact {} identified as {} type", context.getRootArtifact(), rootArtifactType);

    PluginSharedLibClassifier pluginSharedLibClassifier = new PluginSharedLibClassifier();
    final List<ArtifactUrlClassification> pluginSharedLibUrls = pluginSharedLibClassifier
        .classify(dependencyResolver, context, directDependencies, context.getRootArtifact(), rootArtifactType);

    PluginsClassifier pluginsClassifier = new PluginsClassifier();
    final List<PluginUrlClassification> pluginUrlClassifications =
        pluginsClassifier.classify(dependencyResolver, context, directDependencies, context.getRootArtifact(), rootArtifactType);

    ServicesClassifier servicesClassifier = new ServicesClassifier();
    final List<ArtifactUrlClassification> serviceClassifications =
        servicesClassifier.classify(dependencyResolver, context, directDependencies, context.getRootArtifact(), rootArtifactType);



    //List<ArtifactUrlClassification> serviceUrlClassifications = buildServicesUrlClassification(context, directDependencies);

    //List<URL> pluginSharedLibUrls = buildPluginSharedLibClassification(context, directDependencies);
    //List<PluginUrlClassification> pluginUrlClassifications =
    //    buildPluginUrlClassifications(context, directDependencies, rootArtifactType);
    //
    //List<ArtifactUrlClassification> serviceUrlClassifications = buildServicesUrlClassification(context, directDependencies);
    //
    //List<URL> containerUrls =
    //    buildContainerUrlClassification(context, directDependencies, serviceUrlClassifications, pluginUrlClassifications,
    //                                    rootArtifactType);
    //List<URL> applicationUrls = buildApplicationUrlClassification(context, directDependencies, rootArtifactType);

    return new ArtifactsUrlClassification(containerUrls, serviceClassifications, pluginSharedLibUrls, pluginUrlClassifications,
                                          applicationUrls);
  }

}
