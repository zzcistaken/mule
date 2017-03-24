/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.service.tls.api;

import org.mule.runtime.api.service.Service;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.api.tls.TlsContextFactoryBuilder;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by anafelisatti on 3/23/17.
 */
public interface TlsService extends Service {

  /**
   * @return the default instance for this container
   */
  TlsContextFactory getDefaultTlsContextFactory();

  /**
   *
   * @return a builder for tls context factories
   */
  TlsContextFactoryBuilder getTlsContextFactoryBuilder();

  /**
   * will use the tls context factory to create a socket factory
   * @param tlsContextFactory
   * @return
   */
  SSLSocketFactory createSslSocketFactory(TlsContextFactory tlsContextFactory);

  /**
   * will use the tls context factory to create a server socket factory
   * @param tlsContextFactory
   * @return
   */
  SSLServerSocketFactory createSslServerSocketFactory(TlsContextFactory tlsContextFactory);

}
