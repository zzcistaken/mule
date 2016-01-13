package org.mule.api.lang;

import java.util.Collections;
import java.util.List;

public class ConfigFile {

    private String filename;
    private List<ConfigLine> configLines;

    public ConfigFile(String filename, List<ConfigLine> configLines) {
        this.filename = filename;
        this.configLines = configLines;
    }

    public String getFilename() {
        return filename;
    }

    public List<ConfigLine> getConfigLines() {
        return Collections.unmodifiableList(configLines);
    }
}
