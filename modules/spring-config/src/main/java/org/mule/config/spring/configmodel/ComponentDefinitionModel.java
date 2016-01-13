package org.mule.config.spring.configmodel;

import java.io.Serializable;
import java.util.*;

/**
 * An ComponentDefinitionModel represents the definition of a component (flow, config, message processor, etc) in the
 * mule artifact configuration.
 */
public class ComponentDefinitionModel {

    private String identifier;
    private Map<String, Serializable> metadata = new HashMap<>();
    private Map<String, Attribute> attributes = new HashMap<>();

    public String getIdentifier() {
        return identifier;
    }

    public Optional<Attribute> getAttribute(String attributeName) {
        return Optional.ofNullable(attributes.get(attributeName));
    }

    public Set<Attribute> getAttributes()
    {
        return Collections.unmodifiableSet(new TreeSet<>(attributes.values()));
    }

    public Map<String, Serializable> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }
}
