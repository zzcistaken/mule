/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.jersey;

import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;

final class StaticJaxbContextResolver implements ContextResolver<JAXBContext>
{

    private JAXBContext jaxbContext;

    StaticJaxbContextResolver(JAXBContext jaxbContext)
    {
        this.jaxbContext = jaxbContext;
    }

    @Override
    public JAXBContext getContext(Class<?> type)
    {
        return jaxbContext;
    }
}
