/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.module.oauth2.internal.clientcredentials;

import static org.mule.extension.module.oauth2.internal.OAuthConstants.ACCESS_TOKEN_EXPRESSION;
import static org.mule.extension.module.oauth2.internal.OAuthConstants.EXPIRATION_TIME_EXPRESSION;
import static org.mule.extension.module.oauth2.internal.OAuthConstants.REFRESH_TOKEN_EXPRESSION;
import static org.mule.extension.module.oauth2.internal.authorizationcode.state.ResourceOwnerOAuthContext.DEFAULT_RESOURCE_OWNER_ID;
import static org.mule.runtime.core.DefaultEventContext.create;
import static org.mule.runtime.core.MessageExchangePattern.REQUEST_RESPONSE;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.initialiseIfNeeded;

import org.mule.extension.module.oauth2.api.TokenNotFoundException;
import org.mule.extension.module.oauth2.internal.AbstractTokenRequestHandler;
import org.mule.extension.module.oauth2.internal.ApplicationCredentials;
import org.mule.extension.module.oauth2.internal.MuleEventLogger;
import org.mule.extension.module.oauth2.internal.OAuthConstants;
import org.mule.extension.module.oauth2.internal.ParameterExtractor;
import org.mule.extension.module.oauth2.internal.authorizationcode.state.ResourceOwnerOAuthContext;
import org.mule.extension.module.oauth2.internal.tokenmanager.TokenManagerConfig;
import org.mule.runtime.core.api.DefaultMuleException;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.lifecycle.Initialisable;
import org.mule.runtime.core.api.lifecycle.InitialisationException;
import org.mule.runtime.core.api.message.InternalMessage;
import org.mule.runtime.core.construct.Flow;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.UseConfig;
import org.mule.runtime.module.http.api.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.codec.binary.Base64;

/**
 * Handler for calling the token url, parsing the response and storing the oauth context data.
 */
@Alias("token-request")
public class ClientCredentialsTokenRequestHandler extends AbstractTokenRequestHandler implements Initialisable {

  /**
   * Scope required by this application to execute. Scopes define permissions over resources.
   */
  @Parameter
  @Optional
  private String scopes;
  private ApplicationCredentials applicationCredentials;

  /**
   * MEL expression to extract the access token parameter from the response of the call to tokenUrl.
   */
  @Parameter
  @Optional(defaultValue = ACCESS_TOKEN_EXPRESSION)
  private Function<Event, String> responseAccessToken;

  @Parameter
  @Optional(defaultValue = REFRESH_TOKEN_EXPRESSION)
  private Function<Event, String> responseRefreshToken;

  /**
   * MEL expression to extract the expiresIn parameter from the response of the call to tokenUrl.
   */
  @Parameter
  @Optional(defaultValue = EXPIRATION_TIME_EXPRESSION)
  private Function<Event, String> responseExpiresIn;

  @Parameter
  @Alias("custom-parameter-extractors")
  private List<ParameterExtractor> parameterExtractors;

  @UseConfig
  private TokenManagerConfig tokenManager;

  /**
   * If true, the client id and client secret will be sent in the request body. Otherwise, they will be sent as basic
   * authentication.
   */
  @Parameter
  @Optional(defaultValue = "false")
  private boolean encodeClientCredentialsInBody;

  private MuleEventLogger muleEventLogger;

  public void setApplicationCredentials(ApplicationCredentials applicationCredentials) {
    this.applicationCredentials = applicationCredentials;
  }

  public void setScopes(String scopes) {
    this.scopes = scopes;
  }

  @Override
  public void initialise() throws InitialisationException {
    muleEventLogger = new MuleEventLogger(logger, muleContext);
    initialiseIfNeeded(tokenManager, muleContext);
  }

  private Event setMapPayloadWithTokenRequestParameters(final Event event) throws MuleException {
    final HashMap<String, String> formData = new HashMap<>();
    formData.put(OAuthConstants.GRANT_TYPE_PARAMETER, OAuthConstants.GRANT_TYPE_CLIENT_CREDENTIALS);
    String clientId = applicationCredentials.getClientId();
    String clientSecret = applicationCredentials.getClientSecret();

    InternalMessage.Builder builder = InternalMessage.builder(event.getMessage());
    if (encodeClientCredentialsInBody) {
      formData.put(OAuthConstants.CLIENT_ID_PARAMETER, clientId);
      formData.put(OAuthConstants.CLIENT_SECRET_PARAMETER, clientSecret);
    } else {
      String encodedCredentials = Base64.encodeBase64String(String.format("%s:%s", clientId, clientSecret).getBytes());
      builder.addOutboundProperty(HttpHeaders.Names.AUTHORIZATION, "Basic " + encodedCredentials);
    }
    if (scopes != null) {
      formData.put(OAuthConstants.SCOPE_PARAMETER, scopes);
    }
    return Event.builder(event).message(builder.payload(formData).build()).build();
  }

  public void refreshAccessToken() throws MuleException {
    try {
      Flow flow = new Flow("test", getMuleContext());
      Event accessTokenEvent = Event.builder(create(flow, "ClientCredentialsTokenRequestHandler"))
          .message(InternalMessage.builder().nullPayload().build()).exchangePattern(REQUEST_RESPONSE).flow(flow).build();
      accessTokenEvent = setMapPayloadWithTokenRequestParameters(accessTokenEvent);
      final Event response;
      response = invokeTokenUrl(accessTokenEvent);
      TokenResponse tokenResponse = processTokenResponse(response, false);

      if (logger.isDebugEnabled()) {
        logger.debug("Retrieved access token, refresh token and expires from token url are: %s, %s, %s",
                     responseAccessToken, responseRefreshToken, responseExpiresIn);
      }

      if (!tokenResponseContentIsValid(tokenResponse)) {
        throw new TokenNotFoundException(response, tokenResponse.accessToken, tokenResponse.refreshToken);
      }

      final ResourceOwnerOAuthContext defaultUserState =
          tokenManager.getConfigOAuthContext().getContextForResourceOwner(DEFAULT_RESOURCE_OWNER_ID);
      defaultUserState.setAccessToken(tokenResponse.accessToken);
      defaultUserState.setExpiresIn(tokenResponse.expiresIn);
      final Map<String, Object> customResponseParameters = tokenResponse.customResponseParameters;
      for (String paramName : customResponseParameters.keySet()) {
        defaultUserState.getTokenResponseParameters().put(paramName, customResponseParameters.get(paramName));
      }
      tokenManager.getConfigOAuthContext().updateResourceOwnerOAuthContext(defaultUserState);
    } catch (TokenNotFoundException e) {
      logger.error(String
          .format("Could not extract access token or refresh token from token URL. Access token is %s, Refresh token is %s",
                  e.getTokenResponseAccessToken(), e.getTokenResponseRefreshToken()));
      muleEventLogger.logContent(e.getTokenUrlResponse());
      throw new DefaultMuleException(e);
    } catch (TokenUrlResponseException e) {
      logger.error((String.format("HTTP response from token URL %s returned a failure status code", getTokenUrl())));
      muleEventLogger.logContent(e.getTokenUrlResponse());
      throw new DefaultMuleException(e);
    }
  }

  public TokenResponse processTokenResponse(Event muleEvent, boolean retrieveRefreshToken) {
    TokenResponse response = new TokenResponse();

    response.accessToken = responseAccessToken.apply(muleEvent);
    response.accessToken = isEmpty(response.accessToken) ? null : response.accessToken;
    if (response.accessToken == null) {
      logger.error("Could not extract access token from token URL. Expressions used to retrieve access token was "
          + responseAccessToken);
    }
    if (retrieveRefreshToken) {
      response.refreshToken = responseRefreshToken.apply(muleEvent);
      response.refreshToken = isEmpty(response.refreshToken) ? null : response.refreshToken;
    }
    response.expiresIn = responseExpiresIn.apply(muleEvent);
    for (ParameterExtractor parameterExtractor : parameterExtractors) {
      response.customResponseParameters.put(parameterExtractor.getParamName(),
                                            parameterExtractor.getValue().apply(muleEvent));
    }

    return response;
  }

  private boolean isEmpty(String value) {
    return value == null || org.mule.runtime.core.util.StringUtils.isEmpty(value);
  }

  private boolean tokenResponseContentIsValid(TokenResponse response) {
    return response.accessToken != null;
  }

  public void setTokenManager(TokenManagerConfig tokenManager) {
    this.tokenManager = tokenManager;
  }

  public void setEncodeClientCredentialsInBody(boolean encodeClientCredentialsInBody) {
    this.encodeClientCredentialsInBody = encodeClientCredentialsInBody;
  }

  private static class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private String expiresIn;
    private Map<String, Object> customResponseParameters = new HashMap<>();
  }
}
