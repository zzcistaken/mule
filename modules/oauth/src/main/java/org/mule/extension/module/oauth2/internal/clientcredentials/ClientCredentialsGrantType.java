/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.module.oauth2.internal.clientcredentials;

import static java.lang.String.format;
import static org.mule.extension.http.api.HttpHeaders.Names.AUTHORIZATION;
import static org.mule.extension.http.internal.HttpConnector.TLS;
import static org.mule.extension.http.internal.HttpConnector.TLS_CONFIGURATION;
import static org.mule.extension.module.oauth2.internal.authorizationcode.state.ResourceOwnerOAuthContext.DEFAULT_RESOURCE_OWNER_ID;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.initialiseIfNeeded;
import static org.mule.runtime.core.config.i18n.I18nMessageFactory.createStaticMessage;
import static org.mule.runtime.module.http.api.HttpConstants.Protocols.HTTPS;

import org.mule.extension.module.oauth2.api.RequestAuthenticationException;
import org.mule.extension.module.oauth2.internal.AbstractGrantType;
import org.mule.extension.module.oauth2.internal.tokenmanager.TokenManagerConfig;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.api.tls.TlsContextFactoryBuilder;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.MuleRuntimeException;
import org.mule.runtime.core.api.context.MuleContextAware;
import org.mule.runtime.core.api.lifecycle.Initialisable;
import org.mule.runtime.core.api.lifecycle.InitialisationException;
import org.mule.runtime.core.api.lifecycle.Startable;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.module.http.internal.domain.request.HttpRequestBuilder;
import org.mule.runtime.module.tls.api.DefaultTlsContextFactoryBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

import javax.inject.Inject;

/**
 * Authorization element for client credentials oauth grant type
 */
public class ClientCredentialsGrantType extends AbstractGrantType implements Initialisable, Startable, MuleContextAware {

  /**
   * Application identifier as defined in the oauth authentication server.
   */
  @Parameter
  private String clientId;

  /**
   * Application secret as defined in the oauth authentication server.
   */
  @Parameter
  private String clientSecret;

  /**
   * This element configures an automatic flow created by mule that listens in the configured url by the redirectUrl attribute and
   * process the request to retrieve an access token from the oauth authentication server.
   */
  @Parameter
  @ParameterGroup
  private ClientCredentialsTokenRequestHandler tokenRequestHandler;

  /**
   * References a TLS config that will be used to receive incoming HTTP request and do HTTP request during the OAuth dance.
   */
  @Parameter
  @Optional
  @DisplayName(TLS_CONFIGURATION)
  @Placement(tab = TLS, group = TLS_CONFIGURATION)
  private TlsContextFactory tlsContextFactory;

  @Inject
  @DefaultTlsContextFactoryBuilder
  private TlsContextFactoryBuilder defaultTlsContextFactoryBuilder;

  public void setClientId(final String clientId) {
    this.clientId = clientId;
  }

  public void setClientSecret(final String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public void setTokenRequestHandler(final ClientCredentialsTokenRequestHandler tokenRequestHandler) {
    this.tokenRequestHandler = tokenRequestHandler;
  }

  public TlsContextFactory getTlsContext() {
    return tlsContextFactory;
  }

  public void setTlsContext(TlsContextFactory tlsContextFactory) {
    this.tlsContextFactory = tlsContextFactory;
  }

  @Override
  public void start() throws MuleException {
    tokenRequestHandler.refreshAccessToken();
  }

  @Override
  public String getClientSecret() {
    return clientSecret;
  }

  @Override
  public String getClientId() {
    return clientId;
  }

  @Override
  public void initialise() throws InitialisationException {
    if (tokenManager == null) {
      this.tokenManager = TokenManagerConfig.createDefault(muleContext);
    }
    tokenRequestHandler.setApplicationCredentials(this);
    tokenRequestHandler.setTokenManager(tokenManager);

    String protocol;
    try {
      final URL tokenUrl = new URL(tokenRequestHandler.getTokenUrl());
      protocol = tokenUrl.getProtocol();
    } catch (MalformedURLException e) {
      throw new InitialisationException(e, this);
    }

    if (protocol.equals(HTTPS.getScheme()) && tlsContextFactory == null) {
      // MULE-9480
      initialiseIfNeeded(defaultTlsContextFactoryBuilder);
      tlsContextFactory = defaultTlsContextFactoryBuilder.buildDefault();
    }
    if (tlsContextFactory != null) {
      initialiseIfNeeded(tlsContextFactory);
    }

    if (tlsContextFactory != null) {
      tokenRequestHandler.setTlsContextFactory(tlsContextFactory);
    }
    initialiseIfNeeded(tokenRequestHandler, muleContext);
  }

  @Override
  protected Function<Event, String> getRefreshTokenWhen() {
    return tokenRequestHandler.getRefreshTokenWhen();
  }

  public void refreshAccessToken() throws MuleException {
    tokenRequestHandler.refreshAccessToken();
  }

  @Override
  @Deprecated
  public void authenticate(Event muleEvent, HttpRequestBuilder builder) throws MuleException {
    final String accessToken = tokenManager.getConfigOAuthContext()
        .getContextForResourceOwner(DEFAULT_RESOURCE_OWNER_ID).getAccessToken();
    if (accessToken == null) {
      throw new RequestAuthenticationException(createStaticMessage(String
          .format("No access token found. Verify that you have authenticated before trying to execute an operation to the API.")));
    }
    builder.addHeader(AUTHORIZATION, buildAuthorizationHeaderContent(accessToken));
  }

  @Override
  public void authenticate(Event muleEvent, org.mule.extension.http.api.request.builder.HttpRequestBuilder builder)
      throws MuleException {
    final String accessToken =
        tokenManager.getConfigOAuthContext().getContextForResourceOwner(DEFAULT_RESOURCE_OWNER_ID).getAccessToken();
    if (accessToken == null) {
      throw new RequestAuthenticationException(createStaticMessage(format("No access token found."
          + " Verify that you have authenticated before trying to execute an operation to the API.")));
    }
    builder.addHeader(AUTHORIZATION, buildAuthorizationHeaderContent(accessToken));
  }

  @Override
  public boolean shouldRetry(final Event firstAttemptResponseEvent) {
    final Boolean shouldRetryRequest = evaluateShouldRetry(firstAttemptResponseEvent);
    if (shouldRetryRequest) {
      try {
        refreshAccessToken();
      } catch (MuleException e) {
        throw new MuleRuntimeException(e);
      }
    }
    return shouldRetryRequest;
  }

  public void setTokenManager(TokenManagerConfig tokenManager) {
    this.tokenManager = tokenManager;
  }

  @Override
  public void setMuleContext(MuleContext context) {
    super.setMuleContext(context);
    tokenRequestHandler.setMuleContext(context);
  }
}
