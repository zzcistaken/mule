/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import static org.mule.runtime.core.config.i18n.MessageFactory.createStaticMessage;
import static org.mule.runtime.core.util.ClassUtils.withContextClassLoader;

import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.core.api.MuleRuntimeException;
import org.mule.runtime.extension.api.exception.IllegalModelDefinitionException;
import org.mule.runtime.extension.api.introspection.connection.ConnectionProviderFactory;
import org.mule.runtime.module.extension.internal.exception.IllegalConnectionProviderModelDefinitionException;
import org.mule.runtime.module.extension.internal.introspection.describer.model.ConnectionProviderElement;
import org.mule.runtime.module.extension.internal.introspection.describer.model.runtime.TypeWrapper;


/**
 * Creates instances of {@link ConnectionProvider} based on a {@link #}
 *
 * @param <Connection> the generic type for the connections that the created {@link ConnectionProvider providers} produce
 * @since 4.0
 */
final class DefaultConnectionProviderFactory<Connection> implements ConnectionProviderFactory<Connection> {

  private ConnectionProviderElement providerType;
  //private final Class<? extends ConnectionProvider> providerClass;
  private final ClassLoader extensionClassLoader;

  /**
   * Creates a new instance which creates {@link ConnectionProvider} instances of the given {@code providerClass}
   *
   * @param extensionClassLoader the {@link ClassLoader} on which the extension is loaded
   * @throws IllegalModelDefinitionException if {@code providerClass} doesn't implement the {@link ConnectionProvider} interface
   * @throws IllegalArgumentException if {@code providerClass} is not an instantiable type
   */
  DefaultConnectionProviderFactory(ConnectionProviderElement providerType, ClassLoader extensionClassLoader) {
    this.providerType = providerType;

    this.extensionClassLoader = extensionClassLoader;

    if (!providerType.isAssignableTo(new TypeWrapper(ConnectionProvider.class))) {
      throw new IllegalConnectionProviderModelDefinitionException(String
          .format("Class '%s' was specified as a connection provider but it doesn't implement the '%s' interface",
                  providerType.getName(), ConnectionProvider.class.getName()));
    }

    //checkInstantiable(providerClass);
    //this.providerClass = (Class<? extends ConnectionProvider>) providerClass;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConnectionProvider<Connection> newInstance() {
    try {
      final Class<?> connectionClass = providerType.getClassSupplier().get();
      return (ConnectionProvider) withContextClassLoader(extensionClassLoader, connectionClass::newInstance);
    } catch (Exception e) {
      throw new MuleRuntimeException(createStaticMessage("Could not create connection provider of type "
          + providerType.getClassName()), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<? extends ConnectionProvider> getObjectType() {
    return (Class<? extends ConnectionProvider>) providerType.getClassSupplier().get();
  }
}
