package org.mule.config.spring.configmodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An application model defines an intermediate representation of the mule components
 * of an artifact (application or domain).
 *
 * It's used to build the actual implementation of the application.
 */
public class ApplicationDefinitionModel {

    private Map<String, Serializable> metadata = new HashMap<>();

    public Map<String, ComponentDefinitionModel> components = new HashMap<>();

    public Map<String, ComponentDefinitionModel> rootComponents = new HashMap<>();

}
