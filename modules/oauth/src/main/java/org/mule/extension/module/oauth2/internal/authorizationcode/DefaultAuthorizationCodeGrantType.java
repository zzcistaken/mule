/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.module.oauth2.internal.authorizationcode;

import static java.lang.String.format;
import static org.mule.extension.http.api.HttpConstants.HttpStatus.FORBIDDEN;
import static org.mule.extension.http.api.HttpConstants.HttpStatus.UNAUTHORIZED;
import static org.mule.extension.http.api.HttpHeaders.Names.AUTHORIZATION;
import static org.mule.extension.http.internal.HttpConnector.TLS;
import static org.mule.extension.http.internal.HttpConnector.TLS_CONFIGURATION;
import static org.mule.runtime.core.config.i18n.I18nMessageFactory.createStaticMessage;

import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.extension.module.oauth2.api.RequestAuthenticationException;
import org.mule.extension.module.oauth2.internal.AbstractGrantType;
import org.mule.extension.module.oauth2.internal.authorizationcode.state.ConfigOAuthContext;
import org.mule.extension.module.oauth2.internal.tokenmanager.TokenManagerConfig;
import org.mule.runtime.api.message.Attributes;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.MuleRuntimeException;
import org.mule.runtime.core.api.lifecycle.Initialisable;
import org.mule.runtime.core.api.lifecycle.InitialisationException;
import org.mule.runtime.core.api.lifecycle.Startable;
import org.mule.runtime.core.util.StringUtils;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.UseConfig;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.module.http.api.HttpHeaders;
import org.mule.runtime.module.http.internal.domain.request.HttpRequestBuilder;

import javax.inject.Inject;

/**
 * Represents the config element for oauth:authentication-code-config.
 * <p>
 * This config will: - If the authorization-request is defined then it will create a flow listening for an user call to begin the
 * oauth login. - If the token-request is defined then it will create a flow for listening in the redirect uri so we can get the
 * authentication code and retrieve the access token
 */
@Alias("AuthorizationCodeGrantType")
public class DefaultAuthorizationCodeGrantType extends AbstractGrantType
    implements Initialisable, AuthorizationCodeGrantType, Startable {

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
   * Listener configuration to be used instead of localCallbackUrl. Note that if using this you must also provide a
   * localCallbackConfigPath separately.
   */
  @UseConfig
  @Optional
  private org.mule.extension.http.api.listener.HttpListenerConfig localCallbackConfig;

  /**
   * Local path for the listener that will be created according to localCallbackConfig, not required if using localCallbackUrl.
   */
  @Parameter
  @Optional
  private String localCallbackConfigPath;

  /**
   * If this attribute is provided mule will automatically create an endpoint in this url to be able to store the authentication
   * code unless there's already an endpoint registered to manually extract the authorization code.
   */
  @Parameter
  @Optional
  private String localCallbackUrl;

  /**
   * The oauth authentication server will use this url to provide the authentication code to the Mule server so the mule server
   * can retrieve the access token.
   * <p>
   * Note that this must be the externally visible address of the callback, not the local one.
   */
  @Parameter
  private String externalCallbackUrl;

  /**
   * This element configures an automatic flow created by mule to handle
   */
  @Parameter
  @Optional
  private AuthorizationRequestHandler authorizationRequestHandler;

  /**
   * This element configures an automatic flow created by mule that listens in the configured url by the redirectUrl attribute and
   * process the request to retrieve an access token from the oauth authentication server.
   */
  @Parameter
  @Optional
  private AbstractAuthorizationCodeTokenRequestHandler tokenRequestHandler;

  @Inject
  private MuleContext muleContext;

  /**
   * References a TLS config that will be used to receive incoming HTTP request and do HTTP request during the OAuth dance.
   */
  @Parameter
  @Optional
  @DisplayName(TLS_CONFIGURATION)
  @Placement(tab = TLS, group = TLS_CONFIGURATION)
  private TlsContextFactory tlsContextFactory;

  /**
   * The token manager configuration to use for this grant type.
   */
  @UseConfig
  @Optional
  private TokenManagerConfig tokenManagerConfig;

  /**
   * Identifier under which the oauth authentication attributes are stored (accessToken, refreshToken, etc).
   * <p>
   * This attribute is only required when the applications needs to access resources from more than one user in the OAuth
   * authentication server.
   */
  @Parameter
  @Optional
  private String localAuthorizationUrlResourceOwnerId;

  /**
   * Identifier under which the oauth authentication attributes are stored (accessToken, refreshToken, etc).
   * <p>
   * This attribute is only required when the applications needs to access resources from more than one user in the OAuth
   * authentication server.
   */
  @Parameter
  @Optional
  private String resourceOwnerId;

  public void setClientId(final String clientId) {
    this.clientId = clientId;
  }

  public void setClientSecret(final String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public void setAuthorizationRequestHandler(final AuthorizationRequestHandler authorizationRequestHandler) {
    this.authorizationRequestHandler = authorizationRequestHandler;
  }

  public void setTokenRequestHandler(final AbstractAuthorizationCodeTokenRequestHandler tokenRequestHandler) {
    this.tokenRequestHandler = tokenRequestHandler;
  }

  public void setLocalCallbackConfig(org.mule.extension.http.api.listener.HttpListenerConfig localCallbackConfig) {
    this.localCallbackConfig = localCallbackConfig;
  }

  public void setLocalCallbackConfigPath(String localCallbackConfigPath) {
    this.localCallbackConfigPath = localCallbackConfigPath;
  }

  public void setLocalCallbackUrl(String localCallbackUrl) {
    this.localCallbackUrl = localCallbackUrl;
  }

  public void setExternalCallbackUrl(String externalCallbackUrl) {
    this.externalCallbackUrl = externalCallbackUrl;
  }

  @Override
  public org.mule.extension.http.api.listener.HttpListenerConfig getLocalCallbackConfig() {
    return localCallbackConfig;
  }

  @Override
  public String getLocalCallbackConfigPath() {
    return localCallbackConfigPath;
  }

  @Override
  public String getLocalCallbackUrl() {
    return localCallbackUrl;
  }

  @Override
  public String getExternalCallbackUrl() {
    return externalCallbackUrl;
  }

  private String getRefreshTokenWhen() {
    return tokenRequestHandler.getRefreshTokenWhen();
  }

  @Override
  public String getLocalAuthorizationUrlResourceOwnerIdEvaluator() {
    return localAuthorizationUrlResourceOwnerId;
  }

  @Override
  public String getResourceOwnerIdEvaluator() {
    return resourceOwnerId;
  }

  @Override
  public void refreshToken(final Event currentFlowEvent, final String resourceOwnerId) throws MuleException {
    tokenRequestHandler.refreshToken(currentFlowEvent, resourceOwnerId);
  }

  @Override
  public ConfigOAuthContext getUserOAuthContext() {
    return tokenManagerConfig.getConfigOAuthContext();
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
  public TlsContextFactory getTlsContext() {
    return tlsContextFactory;
  }

  public void setTlsContext(TlsContextFactory tlsContextFactory) {
    this.tlsContextFactory = tlsContextFactory;
  }

  @Override
  public void initialise() throws InitialisationException {
    try {
      if (tokenManagerConfig == null) {
        this.tokenManagerConfig = TokenManagerConfig.createDefault(muleContext);
        this.tokenManagerConfig.initialise();
      }
      if (localCallbackConfig != null && localCallbackUrl != null) {
        throw new IllegalArgumentException("Attributes localCallbackConfig and localCallbackUrl are mutually exclusive");
      }
      if ((localCallbackConfig == null) != (localCallbackConfigPath == null)) {
        throw new IllegalArgumentException("Attributes localCallbackConfig and localCallbackConfigPath must be both present or absent");
      }
    } catch (Exception e) {
      throw new InitialisationException(e, this);
    }
  }

  @Override
  @Deprecated
  public void authenticate(Event muleEvent, HttpRequestBuilder builder) throws MuleException {
    if (resourceOwnerId == null) {
      throw new RequestAuthenticationException(createStaticMessage(format("Evaluation of %s return an empty resourceOwnerId",
                                                                          localAuthorizationUrlResourceOwnerId)));
    }
    final String accessToken = getUserOAuthContext().getContextForResourceOwner(resourceOwnerId).getAccessToken();
    if (accessToken == null) {
      throw new RequestAuthenticationException(createStaticMessage(format("No access token for the %s user. Verify that you have authenticated the user before trying to execute an operation to the API.",
                                                                          resourceOwnerId)));
    }
    builder.addHeader(HttpHeaders.Names.AUTHORIZATION, buildAuthorizationHeaderContent(accessToken));
  }


  @Override
  public void authenticate(Event muleEvent, org.mule.extension.http.api.request.builder.HttpRequestBuilder builder)
      throws MuleException {
    if (resourceOwnerId == null) {
      throw new RequestAuthenticationException(createStaticMessage(format("Evaluation of %s return an empty resourceOwnerId",
                                                                          localAuthorizationUrlResourceOwnerId)));
    }
    final String accessToken = getUserOAuthContext().getContextForResourceOwner(resourceOwnerId).getAccessToken();
    if (accessToken == null) {
      throw new RequestAuthenticationException(createStaticMessage(format("No access token for the %s user."
          + " Verify that you have authenticated the user before trying to execute an operation to the API.",
                                                                          resourceOwnerId)));
    }
    builder.addHeader(AUTHORIZATION, buildAuthorizationHeaderContent(accessToken));
  }

  @Override
  public boolean shouldRetry(final Event firstAttemptResponseEvent) throws MuleException {
    Boolean shouldRetryRequest = evaluateShouldRetry(firstAttemptResponseEvent);
    if (shouldRetryRequest) {
      try {
        refreshToken(firstAttemptResponseEvent, resourceOwnerId);
      } catch (MuleException e) {
        throw new MuleRuntimeException(e);
      }
    }
    return shouldRetryRequest;
  }

  protected boolean evaluateShouldRetry(final Event firstAttemptResponseEvent) {
    if (!StringUtils.isBlank(getRefreshTokenWhen())) {
      final Object value = muleContext.getExpressionLanguage().evaluate(getRefreshTokenWhen(), firstAttemptResponseEvent, null);
      if (!(value instanceof Boolean)) {
        throw new MuleRuntimeException(createStaticMessage("Expression %s should return a boolean but return %s",
                                                           getRefreshTokenWhen(), value));
      }
      return (Boolean) value;
    } else {
      final Attributes attributes = firstAttemptResponseEvent.getMessage().getAttributes();
      if (attributes instanceof HttpResponseAttributes) {
        return ((HttpResponseAttributes) attributes).getStatusCode() == UNAUTHORIZED.getStatusCode()
            || ((HttpResponseAttributes) attributes).getStatusCode() == FORBIDDEN.getStatusCode();
      } else {
        return false;
      }
    }
  }

  public void setLocalAuthorizationUrlResourceOwnerId(final String resourceOwnerId) {
    localAuthorizationUrlResourceOwnerId = resourceOwnerId;
  }

  public void setResourceOwnerId(String resourceOwnerId) {
    this.resourceOwnerId = resourceOwnerId;
  }

  public void setTokenManager(TokenManagerConfig tokenManagerConfig) {
    this.tokenManagerConfig = tokenManagerConfig;
  }

  @Override
  public void start() throws MuleException {
    if (authorizationRequestHandler != null) {
      authorizationRequestHandler.setOauthConfig(this);
      authorizationRequestHandler.init();
    }
    if (tokenRequestHandler != null) {
      tokenRequestHandler.setOauthConfig(this);
      tokenRequestHandler.init();
    }
  }
}
