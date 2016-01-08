/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.container;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuleOsgiContainer
{

    private static final transient Logger LOGGER = LoggerFactory.getLogger(MuleOsgiContainer.class);
    public static final String STARTUP_BUNDLES_FILE = "startupBundles.properties";

    private static Framework framework = null;
    private final String[] args;
    private int initialBundleStartLevel = 0;

    public MuleOsgiContainer(String[] args)
    {
        this.args = args;
    }

    //TODO(pablo.kraan): OSGi - remove this unused parameter
    public void start(boolean registerShutdownHook)
    {
        //TODO(pablo.kraan): OSGi - show splash screen
        try
        {
            FrameworkFactory factory = getFrameworkFactory();

            Map<String, String> configProperties = new HashMap<>();
            configProperties.put("org.osgi.framework.bsnversion", "multiple");
            configProperties.put("org.osgi.framework.system.packages.extra", "sun.misc");
            //TODO(pablo.kraan): OSGi - this is needed only if we will use "workspace repository"
            //TODO(pablo.kraan): OSGi - available repositories should be maintained externalized on a file
            configProperties.put("org.ops4j.pax.url.mvn.localRepository", MuleFoldersUtil.getRepositoryFolder().toURI().toString() + "/@id=mule.repo");
            configProperties.put("org.ops4j.pax.url.mvn.repositories", "");
            //configProperties.put("org.ops4j.pax.url.mvn.defaultRepositories", "file:///Users/pablokraan/devel/workspaces/muleFull-4.x2/repository,file:///Users/pablokraan/.m2/repository");

            framework = factory.newFramework(configProperties);

            // Initialize the framework, but don't start it yet.
            framework.init();

            FrameworkStartLevel sl = framework.adapt(FrameworkStartLevel.class);
            initialBundleStartLevel = sl.getInitialBundleStartLevel();

            BundleContext context = framework.getBundleContext();
            final FrameworkStartupListener startupListener = new FrameworkStartupListener(context, new ContainerStartupListener(framework));
            context.addBundleListener(startupListener);

            final List<File> mavenRepos = new ArrayList<>();
            ////TODO(pablo.kraan): OSGi - need to configure repositories
            //mavenRepos.add(new File("/Users/pablokraan/devel/workspaces/muleFull-4.x2/repository"));
            mavenRepos.add(MuleFoldersUtil.getRepositoryFolder());

            final List<BundleInfo> bundles = getStartupBundles();

            installAndStartBundles(new SimpleMavenResolver(mavenRepos), context, bundles);

            framework.start();
            setStartLevel(80);
        }
        catch (Exception ex)
        {
            System.err.println("Error creating container: " + ex);
            ex.printStackTrace();
            System.exit(-1);
        }

        try
        {
            runUntilShutdown();
        }
        catch (Throwable ex)
        {
            System.err.println("Error occurred shutting down container: " + ex);
            ex.printStackTrace();
        }
        finally
        {
            System.exit(-2);
        }
    }

    private List<BundleInfo> getStartupBundles()
    {
        final Properties properties = new Properties();
        //TODO(pablo.kraan): OSGi - this file must be read from the MULE folder
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(STARTUP_BUNDLES_FILE))
        {
            properties.load(inputStream);
        }
        catch (IOException e)
        {
            throw new IllegalStateException(String.format("Unable to open %s file", STARTUP_BUNDLES_FILE), e);
        }

        List<BundleInfo> bundles = new ArrayList<>();
        for (Object key : properties.keySet())
        {
            try
            {
                BundleInfo bundleInfo = new BundleInfo((String) key, Integer.parseInt(properties.getProperty((String) key)));

                bundles.add(bundleInfo);
            }
            catch (Exception e)
            {
                throw new IllegalStateException("Invalid bundle information format", e);
            }
        }

        return bundles;
    }

    public static void main(String[] args) throws Exception
    {
        final MuleOsgiContainer container = new MuleOsgiContainer(args);
        try
        {
            container.start(false);
        }
        catch (Exception e)
        {
            //TODO(pablo.kraan): OSGi - destroy any acquired resource
            container.LOGGER.error("Cannot start Mule container", e);
        }
    }

    protected static void setStartLevel(int level)
    {
        framework.adapt(FrameworkStartLevel.class).setStartLevel(level);
    }

    private static void installAndStartBundles(SimpleMavenResolver resolver, BundleContext context, List<BundleInfo> bundles)
    {
        for (BundleInfo bundleInfo : bundles)
        {
            try
            {
                URI resolvedURI = resolver.resolve(new URI(bundleInfo.getLocation()));
                Bundle b = context.installBundle(bundleInfo.getLocation(), resolvedURI.toURL().openStream());

                if (!isFragment(b))
                {
                    b.adapt(BundleStartLevel.class).setStartLevel(bundleInfo.getStartLevel());
                    b.start();
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error installing bundle " + bundleInfo, e);
            }
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
        URL url = MuleOsgiContainer.class.getClassLoader().getResource(
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

        throw new Exception("Unable to find framework factory.");
    }

    private static boolean isFragment(Bundle bundle)
    {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }

    public void runUntilShutdown() throws Exception
    {
        while (true)
        {
            FrameworkEvent event = framework.waitForStop(0);
            if (event.getType() == FrameworkEvent.STOPPED_UPDATE)
            {
                // Framework is restarting because of an update
                while (framework.getState() != Bundle.STARTING && framework.getState() != Bundle.ACTIVE)
                {
                    Thread.sleep(10);
                }
            }
            else
            {
                return;
            }
        }
    }

}
