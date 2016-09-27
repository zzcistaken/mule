/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.module.oauth2.internal.authorizationcode;

import static org.mule.runtime.extension.api.introspection.parameter.ExpressionSupport.NOT_SUPPORTED;

import org.mule.extension.module.oauth2.internal.OAuthConstants;
import org.mule.extension.module.oauth2.internal.ParameterExtractor;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;

import java.util.Collections;
import java.util.List;

/**
 * Provides configuration to handle a token url call response.
 */
public class TokenResponseConfiguration {

  /**
   * MEL expression to extract the access token parameter.
   */
  @Parameter
  @Optional
  @Expression(NOT_SUPPORTED)
  private String accessToken = OAuthConstants.ACCESS_TOKEN_EXPRESSION;

  @Parameter
  @Optional
  @Expression(NOT_SUPPORTED)
  private String refreshToken = OAuthConstants.REFRESH_TOKEN_EXPRESSION;

  /**
   * MEL expression to extract the expired in parameter.
   */
  @Parameter
  @Optional
  @Expression(NOT_SUPPORTED)
  private String expiresIn = OAuthConstants.EXPIRATION_TIME_EXPRESSION;

  private List<ParameterExtractor> parameterExtractors = Collections.emptyList();

  public void setParameterExtractors(final List<ParameterExtractor> parameterExtractors) {
    this.parameterExtractors = parameterExtractors;
  }

  public void setAccessToken(final String accessToken) {
    this.accessToken = accessToken;
  }

  public void setRefreshToken(final String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public void setExpiresIn(final String expiresIn) {
    this.expiresIn = expiresIn;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getExpiresIn() {
    return expiresIn;
  }

  public List<ParameterExtractor> getParameterExtractors() {
    return parameterExtractors;
  }
}
