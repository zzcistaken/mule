/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.vm;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import org.mule.api.context.MuleContextFactory;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.functional.junit4.FunctionalTestCase;

import java.io.File;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;

/**
 * Defines a base class for VM transport tests that run inside an OSGi container
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public abstract class AbstractVmOsgiTestCase extends FunctionalTestCase
{

    @Inject
    public BundleContext bundleContext;

    @Configuration
    public Option[] config() {
        //TODO(pablo.kraan): OSGi - need to automatically load the dependencies
        File projectDir = new File(".");

        return options(
                mavenBundle().groupId("com.google.guava").artifactId("guava").versionAsInProject(),
                mavenBundle().groupId("org.apache.geronimo.specs").artifactId("geronimo-j2ee-connector_1.5_spec").versionAsInProject(),
                mavenBundle().groupId("com.github.stephenc.eaio-uuid").artifactId("uuid").versionAsInProject(),
                mavenBundle().groupId("commons-cli").artifactId("commons-cli").versionAsInProject(),
                mavenBundle().groupId("commons-collections").artifactId("commons-collections").versionAsInProject(),
                mavenBundle().groupId("commons-io").artifactId("commons-io").versionAsInProject(),
                mavenBundle().groupId("commons-lang").artifactId("commons-lang").versionAsInProject(),
                mavenBundle().groupId("commons-pool").artifactId("commons-pool").versionAsInProject(),
                mavenBundle().groupId("commons-beanutils").artifactId("commons-beanutils").versionAsInProject(),
                mavenBundle().groupId("org.jgrapht").artifactId("jgrapht-jdk1.5").versionAsInProject(),
                mavenBundle().groupId("org.mule.mvel").artifactId("mule-mvel2").versionAsInProject(),
                mavenBundle().groupId("asm").artifactId("asm-commons").versionAsInProject(),
                mavenBundle().groupId("asm").artifactId("asm").versionAsInProject(),

                bundle(projectDir.toURI().toString() + "/bundles/mule-core-4.0-SNAPSHOT.jar"),

                mavenBundle().groupId("org.apache.servicemix.bundles").artifactId("org.apache.servicemix.bundles.cglib").versionAsInProject(),
                bundle(projectDir.toURI().toString() + "/bundles/mule-module-annotations-4.0-SNAPSHOT.jar"),

                //TODO(pablo.kraan): OSGi - need to obtain this bundles from maven (3.2.10-RELEASE-osgi)
                bundle("file:/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.core-3.2.1.RELEASE.jar"),
                bundle("file:/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.expression-3.2.1.RELEASE.jar"),
                bundle("file:/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.context-3.2.1.RELEASE.jar"),
                bundle("file:/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.context.support-3.2.1.RELEASE.jar"),
                bundle("file:/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.beans-3.2.1.RELEASE.jar"),
                bundle("file:/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.aop-3.2.1.RELEASE.jar"),

                ////TODO(pablo.kraan): OSGi: where does this dependency comes from?
                mavenBundle().groupId("org.apache.servicemix.bundles").artifactId("org.apache.servicemix.bundles.aopalliance").version("1.0_6"),
                mavenBundle().groupId("dom4j").artifactId("dom4j").versionAsInProject(),
                mavenBundle().groupId("org.eclipse.gemini.blueprint").artifactId("gemini-blueprint-io").versionAsInProject(),
                mavenBundle().groupId("org.eclipse.gemini.blueprint").artifactId("gemini-blueprint-core").versionAsInProject(),
                mavenBundle().groupId("org.eclipse.gemini.blueprint").artifactId("gemini-blueprint-extender").versionAsInProject(),


                //TODO(pablo.kraan): OSGi - update muleCommon to use this version
                mavenBundle().groupId("org.antlr").artifactId("antlr-runtime").version("3.5-osgi"),
                mavenBundle().groupId("org.coosproject.messaging.org.apache.xmlbeans").artifactId("org.apache.xmlbeans.xmlbeans").versionAsInProject(),
                mavenBundle().groupId("org.mule.common").artifactId("mule-common").versionAsInProject(),

                bundle(projectDir.toURI().toString() + "/bundles/mule-module-spring-config-4.0-SNAPSHOT.jar"),

                //TODO(pablo.kraan): OSGi -problem: add a wrong transport in the Mule-Transport header and deployment won't fail but there is an exception in the log
                //TODO(pablo.kraan): OSGi -problem: changes in the maven bundle plugin config is not taken from the IDE
                bundle(projectDir.toURI().toString() + "/bundles/mule-transport-vm-4.0-SNAPSHOT.jar"),


                mavenBundle().groupId("commons-dbutils").artifactId("commons-dbutils").versionAsInProject(),
                mavenBundle().groupId("commons-net").artifactId("commons-net").versionAsInProject(),
                mavenBundle().groupId("org.apache.mina").artifactId("mina-core").versionAsInProject(),
                mavenBundle().groupId("org.apache.ftpserver").artifactId("ftpserver-core").versionAsInProject(),
                mavenBundle().groupId("org.apache.ftpserver").artifactId("ftplet-api").versionAsInProject(),
                mavenBundle().groupId("org.apache.sshd").artifactId("sshd-core").versionAsInProject(),
                mavenBundle().groupId("org.bouncycastle").artifactId("bcprov-jdk16").versionAsInProject(),

                //TODO(pablo.kraan): OSGi - need to use this dependency instead of the original from mockito (maybe we can update mockito) or use the new version (1.4)
                mavenBundle().groupId("org.objenesis").artifactId("objenesis").version("1.4"),
                mavenBundle().groupId("org.mockito").artifactId("mockito-core").versionAsInProject(),

                bundle(projectDir.toURI().toString() + "/bundles/mule-tests-unit-4.0-SNAPSHOT.jar"),

                bundle(projectDir.toURI().toString() + "/bundles/mule-tests-functional-4.0-SNAPSHOT.jar"),
                junitBundles()
        );
    }

    @Override
    protected MuleContextFactory createMuleContextFactory()
    {
        DefaultMuleContextFactory defaultMuleContextFactory = new DefaultMuleContextFactory();
        defaultMuleContextFactory.setBundleContext(bundleContext);

        return defaultMuleContextFactory;
    }
}
