/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.config.bootstrap;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.MuleRuntimeException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.MuleRegistry;
import org.mule.api.registry.ObjectProcessor;
import org.mule.api.registry.RegistrationException;
import org.mule.api.registry.Registry;
import org.mule.api.registry.TransformerResolver;
import org.mule.api.transaction.TransactionFactory;
import org.mule.api.transformer.Converter;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.Transformer;
import org.mule.api.util.StreamCloser;
import org.mule.config.i18n.CoreMessages;
import org.mule.osgi.MuleCoreActivator;
import org.mule.registry.MuleRegistryHelper;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.util.ClassUtils;
import org.mule.util.ExceptionUtils;
import org.mule.util.PropertiesUtils;
import org.mule.util.UUID;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * This object will load objects defined in a file called <code>registry-bootstrap.properties</code> into the local registry.
 * This allows modules and transports to make certain objects available by default.  The most common use case is for a
 * module or transport to load stateless transformers into the registry.
 * For this file to be located it must be present in the modules META-INF directory under
 * <pre>META-INF/services/org/mule/config/</pre>
 * <p/>
 * The format of this file is a simple key / value pair. i.e.
 * <pre>
 * myobject=org.foo.MyObject
 * </pre>
 * Will register an instance of MyObject with a key of 'myobject'. If you don't care about the object name and want to
 * ensure that the ojbect gets a unique name you can use -
 * <pre>
 * object.1=org.foo.MyObject
 * object.2=org.bar.MyObject
 * </pre>
 * or
 * <pre>
 * myFoo=org.foo.MyObject
 * myBar=org.bar.MyObject
 * </pre>
 * It's also possible to define if the entry must be applied to a domain, an application, or both by using the parameter applyToArtifactType.
 * <pre>
 * myFoo=org.foo.MyObject will be applied to any mule application since the parameter applyToArtifactType default value is app
 * myFoo=org.foo.MyObject;applyToArtifactType=app will be applied to any mule application
 * myFoo=org.foo.MyObject;applyToArtifactType=domain will be applied to any mule domain
 * myFoo=org.foo.MyObject;applyToArtifactType=app/domain will be applied to any mule application and any mule domain
 * </pre>
 * Loading transformers has a slightly different notation since you can define the 'returnClass' with optional mime type, and 'name'of
 * the transformer as parameters i.e.
 * <pre>
 * transformer.1=org.mule.transport.jms.transformers.JMSMessageToObject,returnClass=byte[]
 * transformer.2=org.mule.transport.jms.transformers.JMSMessageToObject,returnClass=java.lang.String:text/xml, name=JMSMessageToString
 * transformer.3=org.mule.transport.jms.transformers.JMSMessageToObject,returnClass=java.util.Hashtable)
 * </pre>
 * Note that the key used for transformers must be 'transformer.x' where 'x' is a sequential number.  The transformer name will be
 * automatically generated as JMSMessageToXXX where XXX is the return class name i.e. JMSMessageToString unless a 'name'
 * parameter is specified. If no 'returnClass' is specified the default in the transformer will be used.
 * <p/>
 * Note that all objects defined have to have a default constructor. They can implement injection interfaces such as
 * {@link org.mule.api.context.MuleContextAware} and lifecycle interfaces such as {@link org.mule.api.lifecycle.Initialisable}.
 */
public class SimpleRegistryBootstrap implements Initialisable, MuleContextAware
{

    protected final transient Log logger = LogFactory.getLog(getClass());

    public String TRANSFORMER_KEY = ".transformer.";
    public String OBJECT_KEY = ".object.";
    public String SINGLE_TX = ".singletx.";

    private ArtifactType supportedArtifactType = ArtifactType.APP;
    private final RegistryBootstrapDiscoverer discoverer;
    protected MuleContext context;

    public enum ArtifactType
    {
        APP("app"), DOMAIN("domain"), ALL("app/domain");

        public static final String APPLY_TO_ARTIFACT_TYPE_PARAMETER_KEY = "applyToArtifactType";
        private final String artifactTypeAsString;

        ArtifactType(String artifactTypeAsString)
        {
            this.artifactTypeAsString = artifactTypeAsString;
        }

        public String getAsString()
        {
            return this.artifactTypeAsString;
        }

        public static ArtifactType createFromString(String artifactTypeAsString)
        {
            for (ArtifactType artifactType : values())
            {
                if (artifactType.artifactTypeAsString.equals(artifactTypeAsString))
                {
                    return artifactType;
                }
            }
            throw new MuleRuntimeException(CoreMessages.createStaticMessage("No artifact type found for value: " + artifactTypeAsString));
        }
    }

    /**
     * Creates a default SimpleRegistryBootstrap using a {@link org.mule.config.bootstrap.ClassPathRegistryBootstrapDiscoverer}
     * in order to get the Properties resources from the class path.
     */
    public SimpleRegistryBootstrap()
    {
        this(new ClassPathRegistryBootstrapDiscoverer());
    }

    /**
     * Allows to specify a {@link org.mule.config.bootstrap.RegistryBootstrapDiscoverer} to discover the Properties
     * resources to be used.
     * @param discoverer
     */
    public SimpleRegistryBootstrap(RegistryBootstrapDiscoverer discoverer)
    {
        this.discoverer = discoverer;
    }

    /**
     * {@inheritDoc}
     */
    public void setMuleContext(MuleContext context)
    {
        this.context = context;
    }

    private class BoostrapProperty
    {

        private final RegistryBootstrapService service;
        private final Object key;
        private final Object value;

        private BoostrapProperty(RegistryBootstrapService service, Object key, Object value)
        {
            this.service = service;
            this.key = key;
            this.value = value;
        }

        public RegistryBootstrapService getService()
        {
            return service;
        }

        public Object getValue()
        {
            return value;
        }

        public Object getKey()
        {
            return key;
        }
    }


    /** {@inheritDoc} */
    public void initialise() throws InitialisationException
    {
        Collection<ServiceReference<RegistryBootstrapService>> serviceReferences = null;
        try
        {
            serviceReferences = MuleCoreActivator.bundleContext.getServiceReferences(RegistryBootstrapService.class, null);
        }
        catch (InvalidSyntaxException e)
        {
            logger.error("Unable to get RegistryBootstrapDiscoverer services");
        }

        // Merge and process properties
        int objectCounter = 1;
        int transformerCounter = 1;
        List<BoostrapProperty> transformers = new LinkedList<>();
        List<BoostrapProperty> namedObjects = new LinkedList<>();
        List<BoostrapProperty> unnamedObjects = new LinkedList<>();
        List<BoostrapProperty> singleTransactionFactories = new LinkedList<>();

        for (ServiceReference<RegistryBootstrapService> serviceReference : serviceReferences)
        {
            RegistryBootstrapService registryBootstrapService = MuleCoreActivator.bundleContext.getService(serviceReference);

            Properties bootstrapProperties = registryBootstrapService.loadProperties();

            for (Map.Entry entry : bootstrapProperties.entrySet())
            {
                final String key = (String) entry.getKey();
                if (key.contains(OBJECT_KEY))
                {
                    String newKey = key.substring(0, key.lastIndexOf(".")) + objectCounter++;
                    unnamedObjects.add(new BoostrapProperty(registryBootstrapService, newKey, entry.getValue()));
                }
                else if (key.contains(TRANSFORMER_KEY))
                {
                    String newKey = key.substring(0, key.lastIndexOf(".")) + transformerCounter++;
                    transformers.add(new BoostrapProperty(registryBootstrapService, newKey, entry.getValue()));
                }
                else if (key.contains(SINGLE_TX))
                {
                    if (!key.contains(".transaction.resource"))
                    {
                        String transactionResourceKey = key.replace(".transaction.factory", ".transaction.resource");
                        String transactionResource = bootstrapProperties.getProperty(transactionResourceKey);
                        if (transactionResource == null)
                        {
                            throw new InitialisationException(CoreMessages.createStaticMessage(String.format("Theres no transaction resource specified for transaction factory %s", key)), this);
                        }
                        singleTransactionFactories.add(new BoostrapProperty(registryBootstrapService, (String) entry.getValue(), transactionResource));
                    }
                }
                else
                {
                    namedObjects.add(new BoostrapProperty(registryBootstrapService, key, entry.getValue()));
                }
            }
        }

        try
        {
            registerUnnamedObjects(unnamedObjects, context.getRegistry());
            registerTransformers((MuleRegistryHelper) context.getRegistry());
            registerTransformers(transformers, context.getRegistry());
            registerObjects(namedObjects, context.getRegistry());
            registerTransactionFactories(singleTransactionFactories, context);
        }
        catch (Exception e1)
        {
            throw new InitialisationException(e1, this);
        }
    }

    private void registerTransactionFactories(List<BoostrapProperty> singleTransactionFactories, MuleContext context) throws Exception
    {

        for (BoostrapProperty boostrapProperty : singleTransactionFactories)
        {
            String transactionResourceClassNameProperties = (String) boostrapProperty.getValue();
            String transactionFactoryClassName = (String) boostrapProperty.getKey();
            boolean optional = false;
            // reset
            int x = transactionResourceClassNameProperties.indexOf(",");
            if (x > -1)
            {
                Properties p = PropertiesUtils.getPropertiesFromString(transactionResourceClassNameProperties.substring(x + 1), ',');
                optional = p.containsKey("optional");
            }
            final String transactionResourceClassName = (x == -1 ? transactionResourceClassNameProperties : transactionResourceClassNameProperties.substring(0, x));
            try
            {
                Class<?> supportedType = boostrapProperty.getService().forName(transactionResourceClassName);

                context.getTransactionFactoryManager().registerTransactionFactory(supportedType, (TransactionFactory) boostrapProperty.getService().instantiateClass(transactionFactoryClassName));

            }
            catch (NoClassDefFoundError ncdfe)
            {
                throwExceptionIfNotOptional(optional,ncdfe,"Ignoring optional transaction factory: " + transactionResourceClassName);
            }
            catch (ClassNotFoundException cnfe)
            {
                throwExceptionIfNotOptional(optional,cnfe,"Ignoring optional transaction factory: " + transactionResourceClassName);
            }
        }
    }

    private void registerTransformers(List<BoostrapProperty> props, MuleRegistry registry) throws Exception
    {
        String transString;
        String name = null;
        String returnClassString;
        boolean optional = false;

        for (BoostrapProperty boostrapProperty : props)
        {

            transString = (String) boostrapProperty.getValue();
            // reset
            Class<?> returnClass = null;
            returnClassString = null;
            int x = transString.indexOf(",");
            if (x > -1)
            {
                Properties p = PropertiesUtils.getPropertiesFromString(transString.substring(x + 1), ',');
                name = p.getProperty("name", null);
                returnClassString = p.getProperty("returnClass", null);
                optional = p.containsKey("optional");
            }

            final String transClass = (x == -1 ? transString : transString.substring(0, x));
            try
            {
                String mime = null;
                if (returnClassString != null)
                {
                    int i = returnClassString.indexOf(":");
                    if(i > -1)
                    {
                        mime = returnClassString.substring(i + 1);
                        returnClassString = returnClassString.substring(0, i);
                    }
                    if (returnClassString.equals("byte[]"))
                    {
                        returnClass = byte[].class;
                    }
                    else
                    {
                        returnClass = ClassUtils.loadClass(returnClassString, getClass());
                    }
                }
                Transformer trans = (Transformer) boostrapProperty.service.instantiateClass(transClass);
                if (!(trans instanceof DiscoverableTransformer))
                {
                    throw new RegistrationException(CoreMessages.transformerNotImplementDiscoverable(trans));
                }
                if (returnClass != null)
                {
                    trans.setReturnDataType(DataTypeFactory.create(returnClass, mime));
                }
                if (name != null)
                {
                    trans.setName(name);
                }
                else
                {
                    //This will generate a default name for the transformer
                    name = trans.getName();
                    //We then prefix the name to ensure there is less chance of conflict if the user registers
                    // the transformer with the same name
                    trans.setName("_" + name);
                }
                registry.registerTransformer(trans);
            }
            catch (InvocationTargetException itex)
            {
                Throwable cause = ExceptionUtils.getCause(itex);
                throwExceptionIfNotOptional(cause instanceof NoClassDefFoundError && optional, cause, "Ignoring optional transformer: " + transClass);
            }
            catch (NoClassDefFoundError ncdfe)
            {
                throwExceptionIfNotOptional( optional, ncdfe, "Ignoring optional transformer: " + transClass);

            }
            catch (ClassNotFoundException cnfe)
            {
                throwExceptionIfNotOptional( optional, cnfe, "Ignoring optional transformer: " + transClass);
            }

            name = null;
            returnClass = null;

        }

    }

    private void registerTransformers(MuleRegistryHelper registry) throws MuleException
    {
        Map<String, Converter> converters = registry.lookupByType(Converter.class);
        for (Converter converter : converters.values())
        {
            registry.notifyTransformerResolvers(converter, TransformerResolver.RegistryAction.ADDED);
        }
    }

    private void registerObjects(List<BoostrapProperty> boostrapProperties, Registry registry) throws Exception
    {
        for (BoostrapProperty boostrapProperty : boostrapProperties)
        {
            registerObject(boostrapProperty.getService(), (String) boostrapProperty.getKey(), (String) boostrapProperty.getValue(), registry);
        }
    }

    private void registerUnnamedObjects(List<BoostrapProperty> boostrapProperties, Registry registry) throws Exception
    {
        for (BoostrapProperty boostrapProperty : boostrapProperties)
        {
            final String key = String.format("%s#%s", boostrapProperty.getKey(), UUID.getUUID());
            registerObject(boostrapProperty.getService(), key, (String) boostrapProperty.getValue(), registry);
        }
    }

    private void registerObject(RegistryBootstrapService service, String key, String value, Registry registry) throws Exception
    {
        ArtifactType artifactTypeParameterValue = ArtifactType.APP;

        boolean optional = false;
        String className = null;

        try
        {
            int x = value.indexOf(",");
            if (x > -1)
            {
                Properties p = PropertiesUtils.getPropertiesFromString(value.substring(x + 1), ',');
                if (p.containsKey(ArtifactType.APPLY_TO_ARTIFACT_TYPE_PARAMETER_KEY))
                {
                    artifactTypeParameterValue = ArtifactType.createFromString((String) p.get(ArtifactType.APPLY_TO_ARTIFACT_TYPE_PARAMETER_KEY));
                }
                optional = p.containsKey("optional");
                className = value.substring(0, x);
            }
            else
            {
                className = value;
            }

            if (!artifactTypeParameterValue.equals(ArtifactType.ALL) && !artifactTypeParameterValue.equals(supportedArtifactType))
            {
                return;
            }

            Object o = service.instantiateClass(className);
            Class<?> meta = Object.class;

            if (o instanceof ObjectProcessor)
            {
                meta = ObjectProcessor.class;
            }
            else if (o instanceof StreamCloser)
            {
                meta = StreamCloser.class;
            }
            else if (o instanceof BootstrapObjectFactory)
            {
                o = ((BootstrapObjectFactory)o).create();
            }
            registry.registerObject(key, o, meta);
        }
        catch (InvocationTargetException itex)
        {
            Throwable cause = ExceptionUtils.getCause(itex);
            throwExceptionIfNotOptional(cause instanceof NoClassDefFoundError && optional, cause, "Ignoring optional object: " + className);
        }
        catch (NoClassDefFoundError ncdfe)
        {
            throwExceptionIfNotOptional(optional, ncdfe, "Ignoring optional object: " + className);
        }
        catch (ClassNotFoundException cnfe)
        {
            throwExceptionIfNotOptional(optional, cnfe, "Ignoring optional object: " + className);
        }
    }

    private void throwExceptionIfNotOptional(boolean optional, Throwable t, String message) throws Exception
    {
        if (optional)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(message);
            }
        }
        else if ( t instanceof Exception)
        {
            throw (Exception)t;
        }
        else
        {
            throw new Exception(t);
        }
    }

    /**
     * This attributes define which types or registry bootstrap entries will be
     * created depending on the entry applyToArtifactType parameter value.
     *
     * @param supportedArtifactType type of the artifact to support.
     */
    public void setSupportedArtifactType(ArtifactType supportedArtifactType)
    {
        this.supportedArtifactType = supportedArtifactType;
    }
}
