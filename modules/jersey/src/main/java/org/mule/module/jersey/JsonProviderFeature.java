/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.jersey;

import javax.ws.rs.Priorities;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.InternalProperties;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.message.filtering.SecurityEntityFilteringFeature;
import org.glassfish.jersey.moxy.internal.MoxyFilteringFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.moxy.json.internal.ConfigurableMoxyJsonProvider;
import org.glassfish.jersey.moxy.json.internal.FilteringMoxyJsonProvider;

public class JsonProviderFeature implements Feature
{
    private final static String JSON_FEATURE = MoxyJsonFeature.class.getSimpleName();

    @Override
    public boolean configure(final FeatureContext context) {
        final Configuration config = context.getConfiguration();

        if (CommonProperties.getValue(config.getProperties(), config.getRuntimeType(),
                                      CommonProperties.MOXY_JSON_FEATURE_DISABLE, Boolean.FALSE, Boolean.class)) {
            return false;
        }

        final String jsonFeature = CommonProperties.getValue(config.getProperties(), config.getRuntimeType(),
                                                             InternalProperties.JSON_FEATURE, JSON_FEATURE, String.class);
        // Other JSON providers registered.
        if (!JSON_FEATURE.equalsIgnoreCase(jsonFeature)) {
            return false;
        }

        // Disable other JSON providers.
        context.property(PropertiesHelper.getPropertyNameForRuntime(InternalProperties.JSON_FEATURE, config.getRuntimeType()),
                         JSON_FEATURE);

        // Set a slightly lower priority of workers than JSON-P so MOXy is not pick-ed up for JsonStructures (if both are used).
        final int workerPriority = Priorities.USER + 2000;

        if (entityFilteringEnabled(config)) {
            context.register(MoxyFilteringFeature.class);
            context.register(FilteringMoxyJsonProvider.class, workerPriority);
        } else {
            context.register(JaxbJsonProvider.class, workerPriority);
        }

        return true;
    }

    private boolean entityFilteringEnabled(final Configuration config) {
        return config.isRegistered(EntityFilteringFeature.class) || config.isRegistered(SecurityEntityFilteringFeature.class);
    }
}
