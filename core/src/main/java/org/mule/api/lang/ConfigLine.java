package org.mule.api.lang;

import org.mule.util.Preconditions;

import java.util.*;

public class ConfigLine {

    ConfigLineProvider parent;
    String namespace;
    String operation;

    Map<String, String> rawAttributes = new HashMap<>();

    List<ConfigLine> childs = new ArrayList<>();

    public String getNamespace() {
        return namespace;
    }

    public String getOperation() {
        return operation;
    }

    public Map<String, String> getRawAttributes() {
        return Collections.unmodifiableMap(rawAttributes);
    }

    public List<ConfigLine> getChilds() {
        return Collections.unmodifiableList(childs);
    }

    public ConfigLine getParent() {
        return parent.getConfigLine();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigLine that = (ConfigLine) o;

        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;
        if (operation != null ? !operation.equals(that.operation) : that.operation != null) return false;
        if (rawAttributes != null ? !rawAttributes.equals(that.rawAttributes) : that.rawAttributes != null)
            return false;
        return childs != null ? childs.equals(that.childs) : that.childs == null;

    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        result = 31 * result + (rawAttributes != null ? rawAttributes.hashCode() : 0);
        result = 31 * result + (childs != null ? childs.hashCode() : 0);
        return result;
    }

    public static class ConfigLineBuilder {
        private ConfigLine configLine = new ConfigLine();
        private boolean alreadyBuild;

        public ConfigLineBuilder setNamespace(String namespace) {
            Preconditions.checkState(!alreadyBuild, "builder already build an object, you cannot modify it");
            configLine.namespace = namespace;
            return this;
        }

        public ConfigLineBuilder setOperation(String operation) {
            Preconditions.checkState(!alreadyBuild, "builder already build an object, you cannot modify it");
            configLine.operation = operation;
            return this;
        }

        public ConfigLineBuilder addAttribute(String name, String value)
        {
            Preconditions.checkState(!alreadyBuild, "builder already build an object, you cannot modify it");
            configLine.rawAttributes.put(name, value);
            return this;
        }

        public ConfigLineBuilder addChild(ConfigLine line)
        {
            Preconditions.checkState(!alreadyBuild, "builder already build an object, you cannot modify it");
            configLine.childs.add(line);
            return this;
        }

        public ConfigLineBuilder setParent(ConfigLineProvider parent)
        {
            Preconditions.checkState(!alreadyBuild, "builder already build an object, you cannot modify it");
            configLine.parent = parent;
            return this;
        }

        public ConfigLine build()
        {
            alreadyBuild = true;
            return configLine;
        }

    }

}
