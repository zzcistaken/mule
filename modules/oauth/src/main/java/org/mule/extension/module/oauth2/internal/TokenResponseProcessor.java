/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.module.oauth2.internal;

import org.mule.extension.module.oauth2.internal.authorizationcode.TokenResponseConfiguration;
import org.mule.runtime.core.api.Event;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process a token url response and extracts all the oauth context variables based on the user configuration.
 */
public class TokenResponseProcessor {

  protected Logger logger = LoggerFactory.getLogger(getClass());
  private final TokenResponseConfiguration tokenResponseConfiguration;
  private final boolean retrieveRefreshToken;
  private String accessToken;
  private String refreshToken;
  private String expiresIn;
  private Map<String, Object> customResponseParameters;

  public static TokenResponseProcessor createAuthorizationCodeProcessor(final TokenResponseConfiguration tokenResponseConfiguration) {
    return new TokenResponseProcessor(tokenResponseConfiguration, true);
  }

  private TokenResponseProcessor(final TokenResponseConfiguration tokenResponseConfiguration, boolean retrieveRefreshToken) {
    this.tokenResponseConfiguration = tokenResponseConfiguration;
    this.retrieveRefreshToken = retrieveRefreshToken;
  }

  public void process(Event muleEvent) {
    accessToken = tokenResponseConfiguration.getAccessToken().apply(muleEvent);
    accessToken = isEmpty(accessToken) ? null : accessToken;
    if (accessToken == null) {
      logger.error("Could not extract access token from token URL. Expressions used to retrieve access token was "
          + tokenResponseConfiguration.getAccessToken());
    }
    if (retrieveRefreshToken) {
      refreshToken = tokenResponseConfiguration.getRefreshToken().apply(muleEvent);
      refreshToken = isEmpty(refreshToken) ? null : refreshToken;
    }
    expiresIn = tokenResponseConfiguration.getExpiresIn().apply(muleEvent);
    customResponseParameters = new HashMap<>();
    for (ParameterExtractor parameterExtractor : tokenResponseConfiguration.getParameterExtractors()) {
      customResponseParameters.put(parameterExtractor.getParamName(),
                                   parameterExtractor.getValue().apply(muleEvent));
    }
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

  public Map<String, Object> getCustomResponseParameters() {
    return customResponseParameters;
  }

  private boolean isEmpty(String value) {
    // TODO remove "null" check when MULE-8281 gets fixed.
    return value == null || org.mule.runtime.core.util.StringUtils.isEmpty(value) || "null".equals(value);
  }
}
