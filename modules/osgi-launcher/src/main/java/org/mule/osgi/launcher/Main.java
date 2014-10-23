/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.FrameworkWiring;

/**
 *
 */
public class Main
{

    public static final String BASE_FOLDER = "/Users/pablokraan/devel/osgiDemo/";
    private static Framework framework = null;

    public static void main(String[] args) throws Exception
    {
        System.setProperty("mule.server.baseFolder", BASE_FOLDER);

        // Register a shutdown hook to make sure the framework is
        // cleanly shutdown when the VM exits.
        registerShutDownHook();

        try
        {
            FrameworkFactory factory = getFrameworkFactory();

            Map<String, String> configProperties = new HashMap<>();
            configProperties.put("org.osgi.framework.bsnversion", "multiple");
            //configProperties.put("org.osgi.framework.system.packages.extra", "sun.misc");
            framework = factory.newFramework(configProperties);

            // Initialize the framework, but don't start it yet.
            framework.init();

            BundleContext context = framework.getBundleContext();
            MuleOsgiListener listener = new MuleOsgiListener();
            context.addFrameworkListener(listener);

            Map installedBundleMap = getInstalledBundles(context);

            List<Bundle> bundlesToStart = getBundlesToStart(context, installedBundleMap);

            undeployBundles(installedBundleMap);

            startBundles(bundlesToStart);


            FrameworkEvent event;
            do
            {
                // Start the framework.
                framework.start();

                showBundleStatuses(context);
                showDependencies(context);

                System.out.println("***************************");
                System.out.println("Mule OSGi Container started");
                System.out.println("***************************");

                // Wait for framework to stop to exit the VM.
                event = framework.waitForStop(0);
            }

            // If the framework was updated, then restart it.
            while (event.getType() == FrameworkEvent.STOPPED_UPDATE);

            // Otherwise, exit.
            System.exit(0);
        }
        catch (Exception ex)
        {
            System.err.println("Could not create framework: " + ex);
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private static List<Bundle> getBundlesToStart(BundleContext context, Map installedBundleMap)
    {
        List<Bundle> bundlesToStart = new ArrayList<>();
        bundlesToStart.addAll(installBundles(context, 1, installedBundleMap, getCoreBundles()));

        bundlesToStart.addAll(installBundles(context, 1, installedBundleMap, getSpringConfigBundles()));

        bundlesToStart.addAll(installBundles(context, 1, installedBundleMap, getDeployerBundles()));

        return bundlesToStart;
    }

    private static Map getInstalledBundles(BundleContext context)
    {
        Map installedBundleMap = new HashMap();
        Bundle[] bundles = context.getBundles();
        for (int i = 0; i < bundles.length; i++)
        {
            System.out.println("Installed bundle: " + bundles[i].getLocation());
            installedBundleMap.put(bundles[i].getLocation(), bundles[i]);
        }
        return installedBundleMap;
    }

    private static void startBundles(List<Bundle> bundlesToStart) throws BundleException
    {
        for (Bundle bundle : bundlesToStart)
        {
            System.out.println("Starting bundle: " + bundle.getSymbolicName());
            bundle.start();
        }
    }

    private static void undeployBundles(Map installedBundleMap)
    {
        // Undeploys deleted bundles and all the application bundles
        for (Iterator it = installedBundleMap.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();
            Bundle b = (Bundle) entry.getValue();
            if (b.getBundleId() != 0)
            {
                try
                {
                    b.uninstall();
                }
                catch (BundleException ex)
                {
                    System.err.println("Auto-deploy uninstall error on bundle " + b.getSymbolicName() + " - "
                                       + ex + ((ex.getCause() != null) ? " - " + ex.getCause() : ""));
                }
            }
        }
    }

    private static List<Bundle> installBundles(BundleContext context, int startLevel, Map installedBundleMap, List jarList)
    {
        List<Bundle> bundles = new ArrayList<>();

        for (int i = 0; i < jarList.size(); i++)
        {
            // Look up the bundle by location, removing it from
            // the map of installed bundles so the remaining bundles
            // indicate which bundles may need to be uninstalled.
            Bundle b = (Bundle) installedBundleMap.remove(
                    ((File) jarList.get(i)).toURI().toString());

            try
            {
                // If the bundle is not already installed, then install it
                // if the 'install' action is present.
                if (b == null) //&& actionList.contains(AUTO_DEPLOY_INSTALL_VALUE))
                {
                    b = context.installBundle(
                            ((File) jarList.get(i)).toURI().toString());
                    //bundleList.add(b);
                }
                // If the bundle is already installed, then update it
                // if the 'update' action is present.
                else if (b != null) //&& actionList.contains(AUTO_DEPLOY_UPDATE_VALUE))
                {
                    b.update();
                }

                // If we have found and/or successfully installed a bundle,
                // then add it to the list of bundles to potentially start
                // and also set its start level accordingly.
                if (b != null && !isFragment(b))
                {
                    bundles.add(b);
                    b.adapt(BundleStartLevel.class).setStartLevel(startLevel);
                }
            }
            catch (BundleException ex)
            {
                System.err.println("Auto-deploy install error on bundle " + ((File) jarList.get(i)).toURI().toString() + " - "
                                   + ex + ((ex.getCause() != null) ? " - " + ex.getCause() : ""));
            }
        }

        return bundles;
    }

    private static List getCoreBundles()
    {
        List jarList = new ArrayList();
        jarList.add(new File(BASE_FOLDER + "core/geronimo-jta_1.1_spec-1.1.1.jar"));
        jarList.add(new File(BASE_FOLDER + "core/geronimo-j2ee-connector_1.5_spec-2.0.0.jar"));
        jarList.add(new File(BASE_FOLDER + "core/geronimo-jms_1.1_spec-1.1.1.jar"));

        jarList.add(new File(BASE_FOLDER + "core/log4j-core-2.0.2.jar"));
        jarList.add(new File(BASE_FOLDER + "core/log4j-api-2.0.2.jar"));

        jarList.add(new File(BASE_FOLDER + "core/antlr-runtime-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "core/asm-commons-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "core/asm-osgi.jar"));

        jarList.add(new File(BASE_FOLDER + "core/slf4j-api-1.7.7.jar"));
        jarList.add(new File(BASE_FOLDER + "core/log4j-slf4j-impl-2.0.2.jar"));
        jarList.add(new File(BASE_FOLDER + "core/log4j-1.2-api-2.0.2.jar"));
        jarList.add(new File(BASE_FOLDER + "core/log4j-jcl-2.0.2.jar"));
        jarList.add(new File(BASE_FOLDER + "core/jcl-over-slf4j-1.7.7.jar"));

        jarList.add(new File(BASE_FOLDER + "core/commons-logging-1.2.jar"));
        jarList.add(new File(BASE_FOLDER + "core/commons-beanutils-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "core/commons-cli-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "core/commons-collections-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "core/commons-io-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "core/commons-lang-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "core/commons-pool-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "core/eaio-uuid-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "core/guava-16.0.1.jar"));
        jarList.add(new File(BASE_FOLDER + "core/jgrapht-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "core/xmlbeans-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "core/mule-core-4.0-SNAPSHOT.jar"));

        return jarList;
    }

    private static List getSpringConfigBundles()
    {
        List jarList = new ArrayList();
        jarList.add(new File(BASE_FOLDER + "spring-config/cglib-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/mule-module-annotations-4.0-SNAPSHOT.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/org.apache.servicemix.bundles.aopalliance-1.0_6.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/org.springframework.core-3.2.1.RELEASE.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/org.springframework.aop-3.2.1.RELEASE.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/org.springframework.beans-3.2.1.RELEASE.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/org.springframework.context-3.2.1.RELEASE.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/org.springframework.context.support-3.2.1.RELEASE.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/org.springframework.expression-3.2.1.RELEASE.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/gemini-blueprint-io-1.0.2.RELEASE.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/gemini-blueprint-core-1.0.2.RELEASE.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/gemini-blueprint-extender-1.0.2.RELEASE.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/dom4j-osgi.jar"));
        jarList.add(new File(BASE_FOLDER + "spring-config/mule-module-spring-config-4.0-SNAPSHOT.jar"));

        jarList.add(new File(BASE_FOLDER + "spring-config/mule-transport-vm-4.0-SNAPSHOT.jar"));

        return jarList;
    }

    private static List getDeployerBundles()
    {
        List jarList = new ArrayList();
        jarList.add(new File(BASE_FOLDER + "deployer/org.apache.felix.bundlerepository-2.0.2.jar"));
        jarList.add(new File(BASE_FOLDER + "deployer/org.apache.felix.fileinstall-3.4.0.jar"));
        jarList.add(new File(BASE_FOLDER + "deployer/org.eclipse.equinox.region-1.1.0.v20120522-1841.jar"));
        jarList.add(new File(BASE_FOLDER + "deployer/mule-module-osgi-utils-4.0-SNAPSHOT.jar"));
        jarList.add(new File(BASE_FOLDER + "deployer/mule-module-osgi-deployer-4.0-SNAPSHOT.jar"));

        return jarList;
    }

    private static void registerShutDownHook()
    {
        Runtime.getRuntime().addShutdownHook(new Thread("Mule Launcher Shutdown Hook")
        {
            public void run()
            {
                try
                {
                    if (framework != null)
                    {
                        framework.stop();
                        framework.waitForStop(0);
                    }
                }
                catch (Exception ex)
                {
                    System.err.println("Error stopping framework: " + ex);
                }
            }
        });
    }

    private static void showDependencies(BundleContext context)
    {
        FrameworkWiring frameworkWiring = context.getBundle().adapt(FrameworkWiring.class);
        for (Bundle bundle : frameworkWiring.getBundle().getBundleContext().getBundles())
        {
            System.out.println("Dependency closure for bundle: " + bundle.getSymbolicName());
            Collection<Bundle> dependencyClosure = frameworkWiring.getDependencyClosure(Collections.singleton(bundle));
            for (Bundle dependency : dependencyClosure)
            {
                System.out.println("Bundle: " + dependency.getSymbolicName());
            }
        }
    }

    private static void showBundleStatuses(BundleContext context)
    {
        System.out.println("\nBUNDLE STATUS:");

        for (Bundle bundle : context.getBundles())
        {
            System.out.println("Bundle " + bundle.getSymbolicName() + " is in state: " + getBundleState(bundle.getState()));
        }
    }

    private static String getBundleState(int state)
    {
        switch (state)
        {
            case Bundle.INSTALLED:
                return "INSTALLED";
            case Bundle.RESOLVED:
                return "RESOLVED";
            case Bundle.ACTIVE:
                return "ACTIVE";
            case Bundle.UNINSTALLED:
                return "UNINSTALLED";
            case Bundle.STARTING:
                return "STARTING";
            case Bundle.STOPPING:
                return "STOPPING";
            default:
                throw new IllegalStateException("Unknown bundle state: " + state);
        }
    }

    /**
     * Simple method to parse META-INF/services file for framework factory.
     * Currently, it assumes the first non-commented line is the class name
     * of the framework factory implementation.
     *
     * @return The created <tt>FrameworkFactory</tt> instance.
     * @throws Exception if any errors occur.
     */
    private static FrameworkFactory getFrameworkFactory() throws Exception
    {
        URL url = Main.class.getClassLoader().getResource(
                "META-INF/services/org.osgi.framework.launch.FrameworkFactory");
        if (url != null)
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            try
            {
                for (String s = br.readLine(); s != null; s = br.readLine())
                {
                    s = s.trim();
                    // Try to load first non-empty, non-commented line.
                    if ((s.length() > 0) && (s.charAt(0) != '#'))
                    {
                        return (FrameworkFactory) Class.forName(s).newInstance();
                    }
                }
            }
            finally
            {
                if (br != null)
                {
                    br.close();
                }
            }
        }

        throw new Exception("Could not find framework factory.");
    }

    private static boolean isFragment(Bundle bundle)
    {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }
}
