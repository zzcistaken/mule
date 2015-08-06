/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.jersey;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.glassfish.jersey.moxy.json.internal.ConfigurableMoxyJsonProvider;

@Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD, "application/x-javascript"})
@Consumes({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
@Singleton
public class JaxbJsonProvider extends ConfigurableMoxyJsonProvider
{

    private final Map<Class<?>, JAXBContext> contextCache = new ConcurrentHashMap<>();

    @Context
    private Providers providers;

    @Override
    protected JAXBContext getJAXBContext(Class<?> domainClass, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, ?> httpHeaders) throws JAXBException
    {
        JAXBContext jaxbContext = contextCache.get(domainClass);
        if (jaxbContext != null)
        {
            return jaxbContext;
        }

        ContextResolver<JAXBContext> resolver = null;
        if (providers != null)
        {
            resolver = providers.getContextResolver(JAXBContext.class, mediaType);
        }

        jaxbContext = resolver != null
                      ? resolver.getContext(domainClass)
                      : JAXBContextFactory.createContext(new Class[] {domainClass}, null);

        if (jaxbContext != null)
        {
            contextCache.put(domainClass, jaxbContext);
        }
        return jaxbContext;
    }

}
