/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.runner.api;

import java.util.Set;

import org.eclipse.aether.artifact.Artifact;

/**
 * Resources exported by a plugin.
 *
 * @since 4.0
 */
public class PluginExportResources {

  private final Artifact plugin;
  private final Set<String> exportPackages;
  private final Set<String> exportResources;

  /**
   * Creates an instance of the resources exported by a plugin.
   *
   * @param plugin {@link Artifact} the plugin.
   * @param exportPackages {@link Set} of {@link String}s defining the packages exported by the plugin.
   * @param exportResources {@link Set} of {@link String}s defining the resources exported by the plugin.
   */
  public PluginExportResources(Artifact plugin, Set<String> exportPackages, Set<String> exportResources) {
    this.plugin = plugin;
    this.exportPackages = exportPackages;
    this.exportResources = exportResources;
  }

  public Artifact getPlugin() {
    return plugin;
  }

  public Set<String> getExportPackages() {
    return exportPackages;
  }

  public Set<String> getExportResources() {
    return exportResources;
  }

}
