/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.cxf.support;

import org.mule.transport.cxf.CxfConstants;
import org.mule.transport.cxf.i18n.CxfMessages;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.cxf.common.i18n.Message;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.service.factory.DefaultServiceConfiguration;
import org.apache.cxf.service.factory.ReflectionServiceFactoryBean;
import org.apache.cxf.service.factory.ServiceConstructionException;
import org.apache.cxf.wsdl.WSDLManager;

public class ProxyServiceConfiguration extends DefaultServiceConfiguration
{

    private static final Logger LOG = LogUtils.getLogger(ProxyServiceFactoryBean.class);

    /**
     * Override to use port name from service definition in WSDL when we are doing
     * WSDL-first. This is required so that CXF's internal endpointName and port name
     * match and a CXF Service gets created. See:
     * https://issues.apache.org/jira/browse/CXF-1920
     * http://fisheye6.atlassian.com/changelog/cxf?cs=737994
     * 
     * @Override
     */
    @Override
    public QName getEndpointName()
    {
        try
        {
            ReflectionServiceFactoryBean serviceFactory = getServiceFactory();
            String wsdlURL = serviceFactory.getWsdlURL();
            if (wsdlURL != null)
            {
                Definition definition = serviceFactory.getBus()
                    .getExtension(WSDLManager.class)
                    .getDefinition(wsdlURL);
                Service service = getServiceFromDefinition(definition);
                setServiceNamespace(service.getQName().getNamespaceURI());

                String portName = getPortName(serviceFactory);
                Port port = getPortFromService(service, portName);

                return new QName(getServiceNamespace(), port.getName());
            }
            else
            {
                return super.getEndpointName();
            }

        }
        catch (WSDLException e)
        {
            throw new ServiceConstructionException(new Message("SERVICE_CREATION_MSG", LOG), e);
        }
    }

    protected Port getPortFromService(Service service, String portName)
    {
        Port port = null;
        if (portName != null)
        {
            port = service.getPort(portName);
        }
        if (port == null)
        {
            port = ((Port) service.getPorts().values().iterator().next());
            if (portName != null)
            {
                LOG.warning("No port with endpointName='" + portName + "' was found on service '"
                            + service.getQName() + "'. Using the fist one available: '" + port.getName()
                            + "'");
            }
        }
        return port;
    }

    private String getPortName(ReflectionServiceFactoryBean serviceFactory)
    {
        Map<String, Object> serviceProperties = serviceFactory.getProperties();
        if (serviceProperties == null)
        {
            return null;
        }
        else
        {
            return (String) serviceProperties.get(CxfConstants.PORT_NAME);
        }
    }

    protected Service getServiceFromDefinition(Definition definition)
    {
        Service service = definition.getService(getServiceFactory().getServiceQName());
        if (service == null)
        {
            List<QName> probableServices = getProbableServices(definition);
            List<QName> allServices = getAllServices(definition);
            throw new ComponentNotFoundRuntimeException(CxfMessages.invalidOrMissingNamespace(
                getServiceFactory().getServiceQName(), probableServices, allServices));
        }
        return service;
    }

    /**
     * This method returns a list of all the services defined in the definition. Its
     * current purpose is only for generating a better error message when the service
     * cannot be found.
     * 
     * @param definition
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<QName> getAllServices(Definition definition)
    {
        return new LinkedList<QName>(CollectionUtils.select(definition.getServices().keySet(),
            new Predicate()
            {
                public boolean evaluate(Object object)
                {
                    return object instanceof QName;
                }
            }));
    }

    /**
     * This method returns the list of services that matches with the local part of
     * the service QName. Its current purpose is only for generating a better error
     * message when the service cannot be found.
     * 
     * @param definition
     * @return
     */
    protected List<QName> getProbableServices(Definition definition)
    {
        QName serviceQName = getServiceFactory().getServiceQName();
        List<QName> probableServices = new LinkedList<QName>();
        Map<?, ?> services = definition.getServices();
        for (Iterator<?> iterator = services.keySet().iterator(); iterator.hasNext();)
        {
            Object key = iterator.next();
            if (key instanceof QName)
            {
                QName qNameKey = (QName) key;
                if (qNameKey.getLocalPart() != null
                    && qNameKey.getLocalPart().equals(serviceQName.getLocalPart()))
                {
                    probableServices.add(qNameKey);
                }
            }
        }
        return probableServices;
    }
}
