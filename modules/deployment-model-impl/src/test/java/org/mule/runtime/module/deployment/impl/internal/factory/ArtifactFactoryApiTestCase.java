/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.deployment.impl.internal.factory;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.mule.runtime.core.api.config.MuleProperties.MULE_HOME_DIRECTORY_PROPERTY;

import org.mule.runtime.core.util.FileUtils;
import org.mule.runtime.deployment.model.api.application.Application;
import org.mule.runtime.deployment.model.api.application.ApplicationFactory;
import org.mule.runtime.deployment.model.api.domain.Domain;
import org.mule.runtime.deployment.model.api.domain.DomainFactory;
import org.mule.tck.junit4.AbstractMuleTestCase;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class ArtifactFactoryApiTestCase extends AbstractMuleTestCase {

  private static final String DOMAIN_NAME = "test-domain";
  private static final String APP_NAME = "test-app";

  protected File muleHome;
  protected File appsDir;
  protected File domainsDir;
  // protected File containerAppPluginsDir;
  // protected File tmpAppsDir;

  @Before
  public void before() throws IOException {
    final String tmpDir = getProperty("java.io.tmpdir");
    muleHome = new File(new File(tmpDir, "mule home"), getClass().getSimpleName() + currentTimeMillis());
    appsDir = new File(muleHome, "apps");
    appsDir.mkdirs();
    // containerAppPluginsDir = new File(muleHome, CONTAINER_APP_PLUGINS);
    // containerAppPluginsDir.mkdirs();
    // tmpAppsDir = new File(muleHome, "tmp");
    // tmpAppsDir.mkdirs();
    domainsDir = new File(muleHome, "domains");
    domainsDir.mkdirs();
    setProperty(MULE_HOME_DIRECTORY_PROPERTY, muleHome.getCanonicalPath());
  }

  @After
  public void tearDown() throws Exception {
    FileUtils.deleteTree(muleHome);

    // this is a complex classloader setup and we can't reproduce standalone Mule 100%,
    // so trick the next test method into thinking it's the first run, otherwise
    // app resets CCL ref to null and breaks the next test
    Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
  }

  @Test
  @Ignore
  public void application() throws IOException {
    final File appDir = new File(appsDir, APP_NAME);
    appDir.mkdir();
    final Application createdApp = ApplicationFactory.discover().createArtifact(appDir);
  }

  @Test
  public void domain() throws IOException {
    final File domainDir = new File(domainsDir, DOMAIN_NAME);
    domainDir.mkdir();
    final Domain createdDomain = DomainFactory.discover().createArtifact(domainDir);
  }
}
