/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.deployer.descriptor;

import static org.mule.MuleServer.DEFAULT_CONFIGURATION;
import org.mule.MuleServer;
import org.mule.deployer.MuleFoldersUtil;

import java.io.File;

/**
 * Encapsulates defaults when no explicit descriptor provided with an app.
 */
public class EmptyApplicationDescriptor extends ApplicationDescriptor
{

    private String appName;

    public EmptyApplicationDescriptor(String appName)
    {
        this.appName = appName;
        setConfigResources(new String[] {DEFAULT_CONFIGURATION});
        File configPathFile = getMuleAppDefaultConfigFile(appName);
        String configPath = String.format(configPathFile.getAbsolutePath());
        setAbsoluteResourcePaths(new String[] {configPath});
        setConfigResourcesFile(new File[] {configPathFile});
    }

    public String getAppName()
    {
        return appName;
    }

    /**
     * @param appName name of the application
     * @return null if running embedded, otherwise the app default configuration file as a File ref
     */
    private File getMuleAppDefaultConfigFile(String appName)
    {
        return isStandalone() ? new File(MuleFoldersUtil.getAppFolder(appName), DEFAULT_CONFIGURATION) : null;
    }

    /**
     * Whether Mule is running embedded or standalone.
     * @return true if running standalone
     */
    public static boolean isStandalone()
    {
        // when embedded, mule.home var is not set
        return MuleFoldersUtil.getMuleHomeFolder() != null;
    }

}
