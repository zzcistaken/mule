/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.feature.model;

import java.util.ArrayList;
import java.util.List;

public class MuleCoreFeature extends FeatureInfo
{

    public MuleCoreFeature()
    {
        super("mule-core", createDependencies());
    }

    private static List<Dependency> createDependencies()
    {
        final List<Dependency> dependencies = new ArrayList<>();

        //TODO(pablo.kraan): OSGi - should use a default start level if no one is configured
        //mule-logging

        //mule-common
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.xmlbeans/2.4.0_5/jar", 30));
        dependencies.add(new BundleInfo("mvn:com.fasterxml.jackson.core/jackson-core/2.4.3/jar", 30));
        dependencies.add(new BundleInfo("mvn:com.fasterxml.jackson.core/jackson-annotations/2.4.0/jar", 30));
        dependencies.add(new BundleInfo("mvn:com.fasterxml.jackson.core/jackson-databind/2.4.3/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.antlr/antlr-runtime/3.5/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.antlr/stringtemplate/3.2.1/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-io/commons-io/2.4/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.json/json/20140107/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.common/mule-common/3.8.0-SNAPSHOT/jar", 30));

        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.javax-inject/1_2/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.geronimo.specs/geronimo-jta_1.1_spec/1.1.1/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.geronimo.specs/geronimo-jta_1.1_spec/1.1.1/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.geronimo.specs/geronimo-j2ee-connector_1.5_spec/2.0.0/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:com.github.stephenc.eaio-uuid/uuid/3.4.0/jar", 30));
        dependencies.add(new BundleInfo("mvn:com.google.guava/guava/18.0/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-collections/commons-collections/3.2.2/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-beanutils/commons-beanutils/1.9.2/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-cli/commons-cli/1.2/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-lang/commons-lang/2.4/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-pool/commons-pool/1.6/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.jgrapht/jgrapht-jdk1.5/0.7.3/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule/mule-api/1.0.0-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.extensions/mule-extensions-api/1.0.0-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.reflections/reflections/0.9.9/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.mvel/mule-mvel2/2.1.9-MULE-008/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-beanutils/commons-beanutils/1.9.2/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.mule/mule-core/4.0-SNAPSHOT/jar", 30));

        // Spring Support
        //TODO(pablo.kraan): OSGi - maybe this dependency should go inside the container
        dependencies.add(new BundleInfo("mvn:org.apache.felix/org.apache.felix.fileinstall/3.4.2", 11));

        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.aopalliance/1.0_6", 35));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-core/4.1.2.RELEASE_1", 35));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-expression/4.1.2.RELEASE_1", 35));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-beans/4.1.2.RELEASE_1", 35));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-aop/4.1.2.RELEASE_1", 35));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-context/4.1.2.RELEASE_1", 35));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-context-support/4.1.2.RELEASE_1", 35));

        // Spring Config
        //<feature>mule-core</feature>
        //<feature>spring</feature>
        dependencies.add(new BundleInfo("mvn:org.eclipse.gemini.blueprint/gemini-blueprint-io/2.0.0.BUILD-SNAPSHOT/jar", 35));
        dependencies.add(new BundleInfo("mvn:org.eclipse.gemini.blueprint/gemini-blueprint-core/2.0.0.BUILD-SNAPSHOT/jar", 35));
        dependencies.add(new BundleInfo("mvn:org.eclipse.gemini.blueprint/gemini-blueprint-extender/2.0.0.BUILD-SNAPSHOT/jar", 35));
        dependencies.add(new BundleInfo("wrap:mvn:dom4j/dom4j/1.6.1/jar", 35));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.cglib/2.2_2/jar", 35));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-spring-config/4.0-SNAPSHOT/jar", 35));

        //TODO(pablo.kraan): OSGi - need to find a better place for this dependency
        dependencies.add(new BundleInfo("mvn:org.mule.osgi/mule-osgi-support/4.0-SNAPSHOT/jar", 36));

        // Extensions API
        //<feature>mule-core</feature>
        //<feature>mule-spring-config</feature>
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.extensions/mule-extensions-annotations/1.0.0-SNAPSHOT/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-extensions-support/4.0-SNAPSHOT/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-extensions-spring-support/4.0-SNAPSHOT/jar", 40));

        // Extension Validation
        //<feature>mule-extension-api</feature>
        dependencies.add(new BundleInfo("wrap:mvn:commons-digester/commons-digester/1.8/jar", 40));
        dependencies.add(new BundleInfo("mvn:commons-validator/commons-validator/1.4.0/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-validation/4.0-SNAPSHOT/jar", 40));

        // HTTP Connector
        //<feature>mule-core</feature>
        //<feature>mule-spring-config</feature>
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.extensions/mule-extensions-annotations/1.0.0-SNAPSHOT/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-extensions-support/4.0-SNAPSHOT/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-extensions-spring-support/4.0-SNAPSHOT/jar", 40));

        //TODO(pablo.kraan): OSGi - this has to be part of the deployer feature
        dependencies.add(new BundleInfo("mvn:org.mule.osgi/mule-osgi-app-support/4.0-SNAPSHOT/jar", 36));

        // Extension Validation
        //<feature>mule-extension-api</feature>
        dependencies.add(new BundleInfo("wrap:mvn:commons-digester/commons-digester/1.8/jar", 40));
        dependencies.add(new BundleInfo("mvn:commons-validator/commons-validator/1.4.0/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-validation/4.0-SNAPSHOT/jar", 40));

        // HTTP Connector
        //<feature>mule-core</feature>
        //<feature>mule-spring-config</feature>

        // Grizzly
        dependencies.add(new BundleInfo("wrap:mvn:javax.servlet/javax.servlet-api/3.1.0/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/grizzly-framework/2.3.21/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/grizzly-http/2.3.21/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/connection-pool/2.3.21/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/grizzly-http-server/2.3.21/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/grizzly-http-servlet/2.3.21/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/grizzly-websockets/2.3.21/jar", 40));

        // Transport dependencies
        dependencies.add(new BundleInfo("wrap:mvn:commons-codec/commons-codec/1.9/jar", 40));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.transports/mule-transport-tcp/4.0-SNAPSHOT/jar", 40));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.transports/mule-transport-ssl/4.0-SNAPSHOT/jar", 40));
        dependencies.add(new BundleInfo("wrap:mvn:com.ning/async-http-client/1.9.31/jar", 40));
        dependencies.add(new BundleInfo("wrap:mvn:javax.mail/mail/1.4.3/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-http/4.0-SNAPSHOT/jar", 40));

        // App support

        // Sample Mule Application
        dependencies.add(new BundleInfo("mvn:org.mule.osgi/mule-osgi-deployer-api/4.0-SNAPSHOT/jar", 40));

        //TODO(pablo.kraan): OSGi - WARNING - Adding this dependency here just to make development easier at the moment. This must go inside an EE feature
        //dependencies.add(new BundleInfo("mvn:org.ops4j.base/ops4j-base-lang/1.5.0/jar", 39));
        //dependencies.add(new BundleInfo("mvn:biz.aQute.bnd/bndlib/2.4.0/jar", 39));
        //dependencies.add(new BundleInfo("mvn:org.ops4j.pax.swissbox/pax-swissbox-bnd/1.8.2/jar", 40));
        dependencies.add(new BundleInfo("mvn:com.mulesoft.muleesb.modules/mule-module-plugin-ee/4.0-SNAPSHOT/jar", 40));

        dependencies.add(new BundleInfo("mvn:org.mule.osgi/mule-osgi-deployer-api/4.0-SNAPSHOT/jar", 40));
        dependencies.add(new BundleInfo("mvn:org.mule.osgi/mule-osgi-deployer/4.0-SNAPSHOT/jar", 40));

        return dependencies;
    }
}
