/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.functional.classloading.isolation.maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Uses maven <a href="https://github.com/ferstl/depgraph-maven-plugin">https://github.com/ferstl/depgraph-maven-plugin</a> to resolve the dependencies for the test class.

 * <p/>
 * An example of how the plugin has to be defined:
 * <pre>
 * {@code
 * <plugin>
 *     <groupId>com.github.ferstl</groupId>
 *     <artifactId>depgraph-maven-plugin</artifactId>
 *     <version>1.0.4</version>
 *     <configuration>
 *         <showDuplicates>true</showDuplicates>
 *         <showConflicts>true</showConflicts>
 *         <outputFile>${project.build.testOutputDirectory}/dependency-graph.dot</outputFile>
 *     </configuration>
 *     <executions>
 *         <execution>
 *             <id>depgraph-dependencies-graph</id>
 *             <goals>
 *                 <goal>graph</goal>
 *             </goals>
 *             <phase>process-test-resources</phase>
 *         </execution>
 *     </executions>
 * </plugin>
 * }
 * </pre>
 * If the file doesn't exists it will thrown a {@link IllegalStateException} in all of its methods.
 *
 * @since 4.0
 */
public class DependencyGraphMavenDependenciesResolver implements MavenDependenciesResolver
{
    private static final String DEPENDENCIES_GRAPH_ARROW = "->";
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    protected RawDependencyGraphProvider getRawDependencyGraphProvider(){
        return new FileDependencyGraphProvider();
    }
    /**
     * Creates a dependency graph where with all the transitive dependencies, including duplicates. Uses depgraph-maven-plugin
     * in order to generate the dot graph and with the relationships (edges) it generates the {@link DependenciesGraph}.
     *
     * @throws IllegalStateException if the dependencies are empty
     * @return a {@link DependenciesGraph} that holds the rootArtifact, dependencies and transitive dependencies for each dependency.
     * The rootArtifact represents the current maven artifact that the test belongs to.
     */
    @Override
    public DependenciesGraph buildDependencies() throws IllegalStateException
    {
        List<String> rawDependencies = getRawDependencyGraphProvider().getDependencyGraph();

        LinkedHashMap<MavenArtifact, Set<MavenArtifact>> mavenArtifactsDependencies = new LinkedHashMap<>();
        rawDependencies.stream()
                .filter(line -> line.contains(DEPENDENCIES_GRAPH_ARROW)).forEach(line ->
                                                                               {
                                                                                    MavenArtifact from = parseDotDependencyArtifactFrom(line);
                                                                                    MavenArtifact to = parseDotDependencyArtifactTo(line);
                                                                                    if (!mavenArtifactsDependencies.containsKey(from))
                                                                                    {
                                                                                        mavenArtifactsDependencies.put(from, new HashSet<>());
                                                                                    }
                                                                                    mavenArtifactsDependencies.get(from).add(to);
                                                                               }
        );

        if (mavenArtifactsDependencies.isEmpty())
        {
            throw new IllegalStateException("Dependency graph read but no dependencies found, something may be wrong please check graph content");
        }
        MavenArtifact rootArtifact = mavenArtifactsDependencies.keySet().stream().findFirst().get();
        Set<MavenArtifact> dependencies = mavenArtifactsDependencies.get(rootArtifact);
        mavenArtifactsDependencies.remove(rootArtifact);
        return new DependenciesGraph(rootArtifact, dependencies, mavenArtifactsDependencies);
    }

    private MavenArtifact parseDotDependencyArtifactTo(final String line)
    {
        String artifactLine = line.split(DEPENDENCIES_GRAPH_ARROW)[1];
        if (artifactLine.contains("["))
        {
            artifactLine = artifactLine.substring(0, artifactLine.indexOf("["));
        }
        if (artifactLine.contains("\""))
        {
            artifactLine = artifactLine.substring(artifactLine.indexOf("\"") + 1, artifactLine.lastIndexOf("\""));
        }
        return parseMavenArtifact(artifactLine.trim());
    }

    private MavenArtifact parseDotDependencyArtifactFrom(final String line)
    {
        String artifactLine = line.split(DEPENDENCIES_GRAPH_ARROW)[0];
        if (artifactLine.contains("\""))
        {
            artifactLine = artifactLine.substring(artifactLine.indexOf("\"") + 1, artifactLine.lastIndexOf("\""));
        }
        return parseMavenArtifact(artifactLine.trim());
    }

    private MavenArtifact parseMavenArtifact(final String mavenDependencyString)
    {
        String[] tokens = mavenDependencyString.split(MavenArtifact.MAVEN_DEPENDENCIES_DELIMITER);
        String groupId = tokens[0];
        String artifactId = tokens[1];
        String type = tokens[2];
        String version = tokens[3];
        String scope = tokens[4];
        return MavenArtifact.builder().withGroupId(groupId).withArtifactId(artifactId).withType(type).withVersion(version).withScope(scope).build();
    }

}
