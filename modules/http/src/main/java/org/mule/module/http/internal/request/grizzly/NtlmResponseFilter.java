/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.http.internal.request.grizzly;

import static com.ning.http.util.MiscUtil.isNonEmpty;
import static org.mule.module.http.internal.request.grizzly.NtlmResponseFilter.AuthenticationStatus.AUTHORIZED;
import static org.mule.module.http.internal.request.grizzly.NtlmResponseFilter.AuthenticationStatus.COMPLETED;
import static org.mule.module.http.internal.request.grizzly.NtlmResponseFilter.AuthenticationStatus.CONNECTING;
import static org.mule.module.http.internal.request.grizzly.NtlmResponseFilter.AuthenticationStatus.INITIATING;
import static org.mule.module.http.internal.request.grizzly.NtlmResponseFilter.AuthenticationStatus.UNAUTHORIZED;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.net.HttpHeaders;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Realm;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.filter.FilterContext;
import com.ning.http.client.filter.FilterException;
import com.ning.http.client.filter.ResponseFilter;
import com.ning.http.client.ntlm.NTLMEngine;
import com.ning.http.client.ntlm.NTLMEngineException;

import java.util.Collection;
import java.util.List;

public class NtlmResponseFilter implements ResponseFilter
{

    private NTLMEngine ntlmEngine = new NTLMEngine();
    private AuthenticationStatus authStatus = INITIATING;
    private HttpResponseStatus httpResponseStatus;
    private HttpResponseHeaders httpResponseHeaders;

    @Override
    public FilterContext filter(FilterContext ctx) throws FilterException
    {
        // Filter should only run when status code is 401
        if (ctx.getResponseStatus().getStatusCode() == 401)
        {
            FluentCaseInsensitiveStringsMap responseHeaders = ctx.getResponseHeaders().getHeaders();
            List<String> wwwAuth = responseHeaders.get(HttpHeaders.WWW_AUTHENTICATE);
            Collection<String> ntlmAuthHeaders = Collections2.filter(wwwAuth, new Predicate<String>()
            {
                @Override
                public boolean apply(String s)
                {
                    return s.startsWith("NTLM");
                }
            });
            // Filter should only run when auth header includes NTLM
            if (!ntlmAuthHeaders.isEmpty())
            {
                Realm realm = ctx.getRequest().getRealm();
                Request request = ctx.getRequest();
                FluentCaseInsensitiveStringsMap headers = request.getHeaders();
                FilterContext.FilterContextBuilder filterContextBuilder = new FilterContext.FilterContextBuilder(ctx).replayRequest(true);
                if (realm != null && authStatus.equals(INITIATING))
                {
                    String challengeHeader;
                    try
                    {
                        challengeHeader = ntlmEngine.generateType1Msg(realm.getNtlmDomain(), realm.getNtlmHost());
                    }
                    catch (NTLMEngineException e)
                    {
                        //Should do something better here
                        throw new FilterException(e.getMessage());
                    }
                    addNTLMAuthorization(headers, challengeHeader);
                    authStatus = CONNECTING;
                }
                else if (realm != null && authStatus.equals(CONNECTING))
                {
                    try
                    {
                        addType3NTLMAuthorizationHeader(wwwAuth, headers, realm.getPrincipal(), realm.getPassword(), realm.getNtlmDomain(), realm.getNtlmHost());
                    }
                    catch (NTLMEngineException e)
                    {
                        //Should do something better here
                        throw new FilterException(e.getMessage());
                    }
                    authStatus = COMPLETED;
                }
                else if (authStatus.equals(COMPLETED))
                {
                    authStatus = UNAUTHORIZED;
                    // This will make the 401 get handled by grizzly so an exception will be thrown relating to NTLM not
                    // being supported by them. Saving data to generate a response when catching such exception.
                    httpResponseHeaders = ctx.getResponseHeaders();
                    httpResponseStatus = ctx.getResponseStatus();
                    filterContextBuilder.replayRequest(false);
                }
                Request newRequest = new RequestBuilder(request).setHeaders(headers).build();
                return filterContextBuilder.request(newRequest).build();
            }
        }
        if (authStatus.equals(COMPLETED))
        {
            // Here's hoping a 200 response will make the StatusHandler.InvocationStatus change to STOP
            authStatus = AUTHORIZED;
        }
        return ctx;
    }

    private void addNTLMAuthorization(FluentCaseInsensitiveStringsMap headers, String challengeHeader) {
        headers.add(HttpHeaders.AUTHORIZATION, "NTLM " + challengeHeader);
    }

    private void addType3NTLMAuthorizationHeader(List<String> auth, FluentCaseInsensitiveStringsMap headers, String username, String password, String domain, String workstation)
            throws NTLMEngineException
    {
        headers.remove(HttpHeaders.AUTHORIZATION);

        // This should be improved
        if (isNonEmpty(auth) && auth.get(0).startsWith("NTLM ")) {
            String serverChallenge = auth.get(0).trim().substring("NTLM ".length());
            String challengeHeader = ntlmEngine.generateType3Msg(username, password, domain, workstation, serverChallenge);
            addNTLMAuthorization(headers, challengeHeader);
        }
    }

    public enum AuthenticationStatus
    {
        INITIATING, CONNECTING, COMPLETED, UNAUTHORIZED, AUTHORIZED;
    }

    public HttpResponseStatus getHttpResponseStatus()
    {
        return httpResponseStatus;
    }

    public HttpResponseHeaders getHttpResponseHeaders()
    {
        return httpResponseHeaders;
    }
}
