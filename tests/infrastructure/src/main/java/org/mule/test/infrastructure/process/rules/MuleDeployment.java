/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.infrastructure.process.rules;

import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getProperty;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.moveDirectory;
import static org.apache.commons.lang.StringUtils.isEmpty;

import org.mule.test.infrastructure.process.MuleProcessController;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.StringUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a JUnit rule to install Mule Runtime during tests. Usage:
 *
 * <pre>
 * public static class MuleDeployment {
 *  &#064;Rule
 *  public static MuleDeployment installation = new MuleDeployment(&quot;/path/to/packed/distribution.zip&quot;);
 *
 *  &#064;Test
 *  public void usingMuleRuntime() throws IOException {
 *      String muleHomePath = installation.getMuleHome();
 *      MuleProcessController mule = new MuleProcessController(muleHomePath);
 *      mule.start();
 *     }
 * }
 * </pre>
 *
 */
public class MuleDeployment extends ExternalResource
{

    private static final String DISTRIBUTION_PROPERTY = "com.mulesoft.muleesb.distributions:mule-ee-distribution-standalone:zip";
    private static Logger logger = LoggerFactory.getLogger(MuleDeployment.class);
    private static final File WORKING_DIRECTORY = new File(getProperty("user.dir"));
    public static final String DELETE_ON_EXIT = getProperty("mule.test.deleteOnExit");
    private File distribution;
    private String application;
    private File muleHome;
    private String testname;
    private MuleProcessController mule;
    private Map properties;

    protected MuleDeployment()
    {
        String zippedDistribution = System.getProperty(DISTRIBUTION_PROPERTY);
        if (StringUtils.isEmpty(zippedDistribution))
        {
            logger.error("You must configure the location for Mule distribution in the system property: " + DISTRIBUTION_PROPERTY);
        }
        distribution = new File(zippedDistribution);
        if (!distribution.exists())
        {
            throw new IllegalArgumentException("Packed distribution not found: " + distribution);
        }
    }

    public static class Builder
    {

        MuleDeployment deployment;

        public Builder(String application)
        {
            deployment = new MuleDeployment();
            deployment.application  = application;
        }

        public MuleDeployment deploy()
        {
            return deployment;
        }

        public Builder withProperties(Map properties)
        {
            deployment.properties = properties;
            return this;
        }
    }

    public static MuleDeployment.Builder application(String application)
    {
        return new Builder(application);
    }

    @Override
    protected void before() throws Throwable
    {
        unzip(distribution, WORKING_DIRECTORY);
        mule = new MuleProcessController(muleHome.getAbsolutePath());
        mule.deploy(application);
        mule.start();
    }

    @Override
    protected void after()
    {
        mule.stop();
        File logs = new File(muleHome, "logs");
        File dest = new File(testname + ".logs");
        deleteQuietly(dest);
        if (isEmpty(DELETE_ON_EXIT) || parseBoolean(DELETE_ON_EXIT))
        {
            try
            {
                moveDirectory(logs, dest);
                deleteDirectory(muleHome);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Couldn't delete directory [" + muleHome + "], delete it manually.", e);
            }
        }
    }

    private void unzip(File file, File destDir) throws IOException
    {
        try (ZipFile zip = new ZipFile(file))
        {
            Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();
            ZipEntry root = zipFileEntries.nextElement();
            muleHome = new File(destDir, root.getName());
            muleHome.mkdirs();
            chmodRwx(muleHome);
            while (zipFileEntries.hasMoreElements())
            {
                ZipEntry entry = zipFileEntries.nextElement();
                File destFile = new File(entry.getName());
                if (entry.isDirectory())
                {
                    destFile.mkdir();
                }
                else
                {
                    copyInputStreamToFile(zip.getInputStream(entry), destFile);
                    chmodRwx(destFile);
                }
            }
        }
    }

    private void chmodRwx(File destFile)
    {
        destFile.setExecutable(true, false);
        destFile.setWritable(true, false);
        destFile.setReadable(true, false);
    }

    public String getHome()
    {
        return muleHome.getAbsolutePath();
    }
}
