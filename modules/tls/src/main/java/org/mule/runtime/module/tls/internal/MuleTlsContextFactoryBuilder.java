/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.tls.internal;

import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.api.tls.TlsContextFactoryBuilder;

import java.io.IOException;

public class MuleTlsContextFactoryBuilder implements TlsContextFactoryBuilder {

  private DefaultTlsContextFactory tlsContextFactory = new DefaultTlsContextFactory();

  @Override
  public TlsContextFactory build() {
    try {
      tlsContextFactory.initialise();
    } catch (InitialisationException e) {
      //TODO throw meaningful exception
    }
    return tlsContextFactory;
  }

  @Override
  public TlsContextFactoryBuilder withName(String name) {
    tlsContextFactory.setName(name);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder withEnabledProtocols(String protocols) {
    tlsContextFactory.setEnabledProtocols(protocols);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder witEnabledCipherSuites(String cipherSuites) {
    tlsContextFactory.setEnabledCipherSuites(cipherSuites);
    return null;
  }

  @Override
  public TlsContextFactoryBuilder withTrustStorePath(String path) throws IOException {
    tlsContextFactory.setTrustStorePath(path);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder withTrustStorePassword(String password) {
    tlsContextFactory.setTrustStorePassword(password);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder withTrustStoreType(String type) {
    tlsContextFactory.setTrustStoreType(type);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder withInsecureTrustStore(boolean insecure) {
    tlsContextFactory.setTrustStoreInsecure(insecure);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder withTrustStoreAlgorithm(String algorithm) {
    tlsContextFactory.setTrustManagerAlgorithm(algorithm);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder withKeyStorePath(String path) throws IOException {
    tlsContextFactory.setKeyStorePath(path);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder withKeyStorePassword(String password) {
    tlsContextFactory.setKeyManagerPassword(password);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder withKeyAlias(String alias) {
    tlsContextFactory.setKeyAlias(alias);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder withKeyPassword(String password) {
    tlsContextFactory.setKeyManagerPassword(password);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder withKeyStoreType(String type) {
    tlsContextFactory.setKeyStoreType(type);
    return this;
  }

  @Override
  public TlsContextFactoryBuilder withKeyStoreAlgorithm(String algorithm) {
    tlsContextFactory.setKeyManagerAlgorithm(algorithm);
    return this;
  }

}
