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

    private static Framework framework = null;

    public static void main(String[] args) throws Exception
    {
        // Register a shutdown hook to make sure the framework is
        // cleanly shutdown when the VM exits.
        registerShutDownHook();

        try
        {
            // Create an instance of the framework.
            FrameworkFactory factory = getFrameworkFactory();
            Map<String, String> stringStringMap = new HashMap<>();
            //stringStringMap.put("felix.fileinstall.dir", "/Users/pablokraan/devel/osgiexample/autodeploy/");
            //stringStringMap.put("felix.fileinstall.bundles.updateWithListeners", "true");
            //stringStringMap.put("felix.fileinstall.debug", "2");
            framework = factory.newFramework(stringStringMap);

            // Initialize the framework, but don't start it yet.
            framework.init();

            BundleContext context = framework.getBundleContext();
            MuleOsgiListener listener = new MuleOsgiListener();
            context.addFrameworkListener(listener);
            System.out.println("System start level: " + context.getBundle().adapt(BundleStartLevel.class).getStartLevel());

            // Get list of already installed bundles as a map.
            Map installedBundleMap = new HashMap();
            Bundle[] bundles = context.getBundles();
            for (int i = 0; i < bundles.length; i++)
            {
                System.out.println("Installed bundle: " + bundles[i].getLocation());
                installedBundleMap.put(bundles[i].getLocation(), bundles[i]);
            }

            List<Bundle> bundlesToStart = new ArrayList<>();
            bundlesToStart.addAll(installBundles(context, 1, installedBundleMap, listBundles("")));

            bundlesToStart.addAll(installBundles(context, 1, installedBundleMap, listBundles2("")));

            bundlesToStart.addAll(installBundles(context, 1, installedBundleMap, listBundlesDeployer("")));
            //installBundles(context, 20, installedBundleMap, listBundles2(""));
            //installBundlesFromDir(context, 1, installedBundleMap, "/Users/pablokraan/devel/osgiexample/core");
            //installBundlesFromDir(context, 20, installedBundleMap, "/Users/pablokraan/devel/osgiexample/spring-config");

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

                /*
                //context.installBundle("reference:file:/Users/pablokraan/devel/osgiexample/apps/osgi-example-4.0-SNAPSHOT.jar");

                //File appsFolder = new File("/Users/pablokraan/devel/osgiexample/apps");
                //String[] appFiles = appsFolder.list(new SuffixFileFilter(".zip"));
                //if (appFiles != null)
                //{
                //    for (String appFile : appFiles)
                //    {
                //        //ApplicationBundleBuilder bundleBuilder = new ApplicationBundleBuilder();
                //        //File bundleTempFile = bundleBuilder.build(new File(appsFolder, appFile));
                //        //File bundleFolder = new File(appsFolder, FilenameUtils.getBaseName(appFile));
                //        //org.mule.util.FileUtils.unzip(bundleTempFile, bundleFolder);
                //        //bundleTempFile.renameTo(bundleFile);
                //
                        Bundle bundle = null;
                //        try
                //        {
                //            bundle = context.installBundle("reference:file:/Users/pablokraan/devel/osgiexample/apps/simpleApp/");
                //context.installBundle("reference:file:/Users/pablokraan/devel/osgiexample/apps/simpleApp/");
                //            //bundle = context.installBundle(bundleFolder.toURI().toString());
                //            bundle = context.installBundle("reference:file:/Users/pablokraan/devel/osgiexample/simpleApp/simpleApp.mab");
                //        }
                //        catch (BundleException e)
                //        {
                //            System.err.println("Error installing application bundle: " + e.getMessage());
                //        }
                //
                //        try
                //        {
                //            bundle.start();
                //        }
                //        catch (BundleException e)
                //        {
                //            System.err.println("Error starting application bundle: " + e.getMessage());
                //        }
                //    }
                //}
                 */

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

    private static void startBundles(List<Bundle> bundlesToStart) throws BundleException
    {
        for (Bundle bundle : bundlesToStart)
        {
            System.out.println("Starting bundle: " + bundle.getSymbolicName());
            bundle.start();
        }
    }

    private static void installBundlesFromDir(BundleContext context, int startLevel, Map installedBundleMap, String folder)
    {
        // Look in the specified bundle directory to create a list
        // of all JAR files to install.
        List coreJarList = listBundles(folder);

        // Install bundle JAR files and remember the bundle objects.
        installBundles(context, startLevel, installedBundleMap, coreJarList);
    }

    private static void undeployBundles(Map installedBundleMap)
    {
        // Uninstall all bundles not in the auto-deploy directory if
        // the 'uninstall' action is present.
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

    private static List listBundles(String autoDir)
    {
        //File[] files = new File(autoDir).listFiles();
        //List jarList = new ArrayList();
        //if (files != null)
        //{
        //    Arrays.sort(files);
        //    for (int i = 0; i < files.length; i++)
        //    {
        //        if (files[i].getName().endsWith(".jar"))
        //        {
        //            System.out.println("Auto deploy bundle: " + files[i].getAbsolutePath());
        //            jarList.add(files[i]);
        //        }
        //    }
        //}
        List jarList = new ArrayList();
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/log4j-1.2.17.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/jcl-over-slf4j-1.6.1.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/antlr-runtime-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/asm-commons-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/asm-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/commons-beanutils-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/commons-cli-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/commons-collections-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/commons-io-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/commons-lang-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/commons-pool-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/eaio-uuid-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/guava-16.0.1.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/jgrapht-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/mule-mvel2-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/slf4j-api-1.6.1.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/slf4j-log4j12-1.6.1.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/xmlbeans-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/geronimo-jta_1.1_spec-1.1.1.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/geronimo-j2ee-connector_1.5_spec-2.0.0.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/mule-core-4.0-SNAPSHOT.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/mule-common-4.0-SNAPSHOT.jar"));

        return jarList;
    }

    private static List listBundles2(String autoDir)
    {
        List jarList = new ArrayList();
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/core/antlr-runtime-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/cglib-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/mule-module-annotations-4.0-SNAPSHOT.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/org.apache.servicemix.bundles.aopalliance-1.0_6.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.core-3.2.1.RELEASE.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.aop-3.2.1.RELEASE.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.beans-3.2.1.RELEASE.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.context-3.2.1.RELEASE.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.context.support-3.2.1.RELEASE.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/org.springframework.expression-3.2.1.RELEASE.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/gemini-blueprint-io-1.0.2.RELEASE.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/gemini-blueprint-core-1.0.2.RELEASE.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/gemini-blueprint-extender-1.0.2.RELEASE.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/dom4j-osgi.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/mule-module-spring-config-4.0-SNAPSHOT.jar"));

        jarList.add(new File("/Users/pablokraan/devel/osgiexample/spring-config/mule-transport-vm-4.0-SNAPSHOT.jar"));

        return jarList;
    }

    private static List listBundlesDeployer(String autoDir)
    {
        List jarList = new ArrayList();
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/deployer/org.apache.felix.fileinstall-3.4.0.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/deployer/org.eclipse.equinox.region-1.1.0.v20120522-1841.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/deployer/mule-module-osgi-utils-4.0-SNAPSHOT.jar"));
        jarList.add(new File("/Users/pablokraan/devel/osgiexample/deployer/mule-module-osgi-deployer-4.0-SNAPSHOT.jar"));

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
            //if (bundle.getState() != Bundle.ACTIVE && !isFragment(bundle))
            //{
            //    try
            //    {
            //        bundle.start();
            //    }
            //    catch (BundleException e)
            //    {
            //        System.out.println("Error starting bundle: " + bundle.getSymbolicName() + "\n" + e.getMessage());
            //    }
            //}
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
