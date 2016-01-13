package org.mule.config.spring.language;

import org.mule.api.lang.ConfigFile;
import org.mule.api.lang.ConfigLine;

import java.util.*;

public class ApplicationConfig {

    private String appName;
    private Set<ConfigFile> configFiles = new TreeSet<>();

    private ApplicationConfig()
    {}

    public String getAppName() {
        return appName;
    }

    public Set<ConfigFile> getConfigFiles() {
        return Collections.unmodifiableSet(configFiles);
    }

    public List<ConfigLine> getAllConfigLines()
    {
        List<ConfigLine> allConfigLines = new ArrayList<>();
        configFiles.stream().forEach(configFile -> {
            allConfigLines.addAll(configFile.getConfigLines());
        });
        return allConfigLines;
    }

    public static class Builder
    {
        private ApplicationConfig applicationConfig = new ApplicationConfig();

        public Builder setAppName(String appName)
        {
            this.applicationConfig.appName = appName;
            return this;
        }

        public Builder addConfigFile(ConfigFile configFile)
        {
            this.applicationConfig.configFiles.add(configFile);
            return this;
        }

        public ApplicationConfig build()
        {
            return this.applicationConfig;
        }
    }

}
