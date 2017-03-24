/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.service.tls.impl;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.api.tls.TlsContextFactoryBuilder;
import org.mule.runtime.core.api.security.tls.RestrictedSSLServerSocketFactory;
import org.mule.runtime.core.api.security.tls.RestrictedSSLSocketFactory;
import org.mule.runtime.module.tls.internal.DefaultTlsContextFactory;
import org.mule.runtime.module.tls.internal.MuleTlsContextFactoryBuilder;
import org.mule.service.tls.api.TlsService;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by anafelisatti on 3/23/17.
 */
public class TlsServiceImplementation implements TlsService, Startable {

  private DefaultTlsContextFactory defaultTlsContextFactory = new DefaultTlsContextFactory();

  @Override
  public String getName() {
    return "TLS Service";
  }

  @Override
  public void start() throws MuleException {
    defaultTlsContextFactory.initialise();
  }

  @Override
  public TlsContextFactory getDefaultTlsContextFactory() {
    return defaultTlsContextFactory;
  }

  @Override
  public TlsContextFactoryBuilder getTlsContextFactoryBuilder() {
    return new MuleTlsContextFactoryBuilder();
  }

  @Override
  public SSLSocketFactory createSslSocketFactory(TlsContextFactory tlsContextFactory) {
    try {
      return new RestrictedSSLSocketFactory(tlsContextFactory.createSslContext(),
                                            tlsContextFactory.getEnabledCipherSuites(),
                                            tlsContextFactory.getEnabledProtocols());
    } catch (KeyManagementException e) {
      //TODO figure out how to fail nicely
      throw new RuntimeException();
    } catch (NoSuchAlgorithmException e) {
      //TODO figure out how to fail nicely
      throw new RuntimeException();
    }
  }

  @Override
  public SSLServerSocketFactory createSslServerSocketFactory(TlsContextFactory tlsContextFactory) {
    try {
      return new RestrictedSSLServerSocketFactory(tlsContextFactory.createSslContext(),
                                                  tlsContextFactory.getEnabledCipherSuites(),
                                                  tlsContextFactory.getEnabledProtocols());
    } catch (KeyManagementException e) {
      //TODO figure out how to fail nicely
      throw new RuntimeException();
    } catch (NoSuchAlgorithmException e) {
      //TODO figure out how to fail nicely
      throw new RuntimeException();
    }
  }
}
