/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.module.oauth2.internal;

import static org.mule.extension.module.oauth2.internal.OAuthConstants.ACCESS_TOKEN_EXPRESSION;
import static org.mule.extension.module.oauth2.internal.OAuthConstants.EXPIRATION_TIME_EXPRESSION;
import static org.mule.extension.module.oauth2.internal.OAuthConstants.REFRESH_TOKEN_EXPRESSION;
import static org.mule.runtime.module.http.api.HttpConstants.HttpStatus.BAD_REQUEST;
import static org.mule.runtime.module.http.api.HttpConstants.Methods.POST;
import static org.mule.runtime.module.http.api.HttpConstants.ResponseProperties.HTTP_STATUS_PROPERTY;
import static org.mule.runtime.module.http.api.client.HttpRequestOptionsBuilder.newOptions;

import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.context.MuleContextAware;
import org.mule.runtime.core.api.message.InternalMessage;
import org.mule.runtime.core.util.CollectionUtils;
import org.mule.runtime.core.util.IOUtils;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.module.http.api.client.HttpRequestOptions;
import org.mule.runtime.module.http.api.client.HttpRequestOptionsBuilder;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTokenRequestHandler implements MuleContextAware {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  protected MuleContext muleContext;

  /**
   * MEL expression to extract the access token parameter from the response of the call to tokenUrl.
   */
  @Parameter
  @Optional(defaultValue = ACCESS_TOKEN_EXPRESSION)
  protected Function<Event, String> responseAccessToken;

  @Parameter
  @Optional(defaultValue = REFRESH_TOKEN_EXPRESSION)
  protected Function<Event, String> responseRefreshToken;

  /**
   * MEL expression to extract the expiresIn parameter from the response of the call to tokenUrl.
   */
  @Parameter
  @Optional(defaultValue = EXPIRATION_TIME_EXPRESSION)
  protected Function<Event, String> responseExpiresIn;

  @Parameter
  @Alias("custom-parameter-extractors")
  protected List<ParameterExtractor> parameterExtractors;

  /**
   * After executing an API call authenticated with OAuth it may be that the access token used was expired, so this attribute
   * allows a MEL expressions that will be evaluated against the http response of the API callback to determine if the request
   * failed because it was done using an expired token. In case the evaluation returns true (access token expired) then mule will
   * automatically trigger a refresh token flow and retry the API callback using a new access token. Default value evaluates if
   * the response status code was 401 or 403.
   */
  @Parameter
  @Optional
  private Function<Event, String> refreshTokenWhen;

  /**
   * The oauth authentication server url to get access to the token. Mule, after receiving the authentication code from the oauth
   * server (through the redirectUrl) will call this url to get the access token.
   */
  @Parameter
  private String tokenUrl;

  private HttpRequestOptions httpRequestOptions = newOptions().method(POST.name()).disableStatusCodeValidation().build();

  /**
   * @param refreshTokenWhen expression to use to determine if the response from a request to the API requires a new token
   */
  public void setRefreshTokenWhen(Function<Event, String> refreshTokenWhen) {
    this.refreshTokenWhen = refreshTokenWhen;
  }

  public Function<Event, String> getRefreshTokenWhen() {
    return refreshTokenWhen;
  }

  protected MuleContext getMuleContext() {
    return muleContext;
  }

  public void setTokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
  }

  public void setTlsContextFactory(final TlsContextFactory tlsContextFactory) {
    httpRequestOptions = HttpRequestOptionsBuilder.newOptions().method(POST.name()).disableStatusCodeValidation()
        .tlsContextFactory(tlsContextFactory).build();
  }

  protected Event invokeTokenUrl(final Event event) throws MuleException, TokenUrlResponseException {
    final InternalMessage message = muleContext.getClient().send(tokenUrl, event.getMessage(), httpRequestOptions).getRight();
    if (message.<Integer>getInboundProperty(HTTP_STATUS_PROPERTY) >= BAD_REQUEST.getStatusCode()) {
      Event response = Event.builder(event).message(message).build();
      throw new TokenUrlResponseException(response);
    }

    if (message.getPayload().getDataType().isStreamType()) {
      return Event.builder(event).message(InternalMessage.builder(message)
          .payload(IOUtils.toString((InputStream) message.getPayload().getValue())).build()).build();
    } else {
      return Event.builder(event).message(message).build();
    }


  }

  public String getTokenUrl() {
    return tokenUrl;
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
    if (!CollectionUtils.isEmpty(parameterExtractors)) {
      for (ParameterExtractor parameterExtractor : parameterExtractors) {
        response.customResponseParameters.put(parameterExtractor.getParamName(),
                                              parameterExtractor.getValue().apply(muleEvent));
      }
    }

    return response;
  }

  protected boolean tokenResponseContentIsValid(TokenResponse response) {
    return response.getAccessToken() != null;
  }

  protected boolean isEmpty(String value) {
    return value == null || org.mule.runtime.core.util.StringUtils.isEmpty(value) || "null".equals(value);
  }

  protected class TokenUrlResponseException extends Exception {

    private Event tokenUrlResponse;

    public TokenUrlResponseException(final Event tokenUrlResponse) {
      this.tokenUrlResponse = tokenUrlResponse;
    }

    public Event getTokenUrlResponse() {
      return tokenUrlResponse;
    }
  }

  @Override
  public void setMuleContext(MuleContext context) {
    this.muleContext = context;
  }


  protected static class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private String expiresIn;
    private Map<String, Object> customResponseParameters = new HashMap<>();

    public String getAccessToken() {
      return accessToken;
    }

    public String getRefreshToken() {
      return refreshToken;
    }

    public String getExpiresIn() {
      return expiresIn;
    }

    public Map<String, Object> getCustomResponseParameters() {
      return customResponseParameters;
    }
  }
}
