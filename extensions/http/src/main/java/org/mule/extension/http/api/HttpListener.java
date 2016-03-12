/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.http.api;

import static org.mule.module.http.api.HttpConstants.HttpStatus.BAD_REQUEST;
import static org.mule.module.http.api.HttpConstants.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.mule.module.http.api.HttpConstants.Protocols.HTTP;
import org.mule.DefaultMuleEvent;
import org.mule.OptimizedRequestContext;
import org.mule.RequestContext;
import org.mule.api.MessagingException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleRuntimeException;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.construct.FlowConstructAware;
import org.mule.api.context.MuleContextAware;
import org.mule.api.execution.CompletionHandler;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.LifecycleUtils;
import org.mule.api.temporary.MuleMessage;
import org.mule.config.ExceptionHelper;
import org.mule.config.i18n.CoreMessages;
import org.mule.execution.MessageProcessingManager;
import org.mule.extension.api.annotation.Alias;
import org.mule.extension.api.annotation.Parameter;
import org.mule.extension.api.annotation.param.UseConfig;
import org.mule.extension.api.runtime.source.Source;
import org.mule.module.http.api.HttpConstants.HttpStatus;
import org.mule.module.http.api.requester.HttpStreamingType;
import org.mule.module.http.internal.HttpParser;
import org.mule.module.http.internal.domain.ByteArrayHttpEntity;
import org.mule.module.http.internal.domain.request.HttpRequestContext;
import org.mule.module.http.internal.domain.response.HttpResponse;
import org.mule.module.http.internal.listener.HttpRequestParsingException;
import org.mule.module.http.internal.listener.HttpRequestToMuleEvent;
import org.mule.module.http.internal.listener.HttpResponseBuilder;
import org.mule.module.http.internal.listener.HttpThrottlingHeadersMapBuilder;
import org.mule.module.http.internal.listener.ListenerPath;
import org.mule.module.http.internal.listener.RequestHandlerManager;
import org.mule.module.http.internal.listener.async.HttpResponseReadyCallback;
import org.mule.module.http.internal.listener.async.RequestHandler;
import org.mule.module.http.internal.listener.async.ResponseStatusCallback;
import org.mule.module.http.internal.listener.matcher.AcceptsAllMethodsRequestMatcher;
import org.mule.module.http.internal.listener.matcher.ListenerRequestMatcher;
import org.mule.module.http.internal.listener.matcher.MethodRequestMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Alias("listener")
public class HttpListener extends Source<org.mule.api.MuleMessage, HttpAttributes> implements Initialisable, MuleContextAware, FlowConstructAware, Disposable
{

    private static final Logger logger = LoggerFactory.getLogger(HttpListener.class);

    public static final String SERVER_PROBLEM = "Server encountered a problem";

    @Parameter
    private String path;
    @Parameter
    private String allowedMethods;
    @Parameter
    private Boolean parseRequest;
    private MethodRequestMatcher methodRequestMatcher = AcceptsAllMethodsRequestMatcher.instance();
    private MuleContext muleContext;
    private FlowConstruct flowConstruct;
    @UseConfig
    private HttpConfig config;

    private org.mule.module.http.internal.listener.HttpResponseBuilder responseBuilder;
    private org.mule.module.http.internal.listener.HttpResponseBuilder errorResponseBuilder;
    @Parameter
    private HttpStreamingType responseStreamingMode = HttpStreamingType.AUTO;

    private RequestHandlerManager requestHandlerManager;
    private MessageProcessingManager messageProcessingManager;

    private String[] parsedAllowedMethods;
    private ListenerPath listenerPath;

    private HttpThrottlingHeadersMapBuilder httpThrottlingHeadersMapBuilder = new HttpThrottlingHeadersMapBuilder();

    @Override
    public synchronized void start()
    {
        requestHandlerManager.start();
    }

    private RequestHandler getRequestHandler()
    {
        return new RequestHandler()
        {
            @Override
            public void handleRequest(HttpRequestContext requestContext, HttpResponseReadyCallback responseCallback)
            {
                try
                {
                    CompletionHandler<MuleMessage<org.mule.api.MuleMessage, HttpAttributes>, Exception> httpCompletionHandler = new CompletionHandler<MuleMessage<org.mule.api.MuleMessage, HttpAttributes>, Exception>()
                    {
                        @Override
                        public void onCompletion(MuleMessage message)
                        {
                            final org.mule.module.http.internal.domain.response.HttpResponseBuilder responseBuilder = new org.mule.module.http.internal.domain.response.HttpResponseBuilder();
                            final HttpResponse httpResponse;
                            httpResponse = buildResponse((org.mule.api.MuleMessage) message.getPayload(), responseBuilder);
                            responseCallback.responseReady(httpResponse, getResponseFailureCallback(new DefaultMuleEvent((org.mule.api.MuleMessage) message.getPayload(), flowConstruct)));
                        }

                        @Override
                        public void onFailure(Exception exception)
                        {
                            MessagingException messagingException = (MessagingException) exception;
                            //For now let's use the HTTP transport exception mapping since makes sense and the gateway depends on it.
                            String exceptionStatusCode = ExceptionHelper.getTransportErrorMapping(HTTP.getScheme(), messagingException.getClass(), muleContext);
                            Integer statusCodeFromException = exceptionStatusCode != null ? Integer.valueOf(exceptionStatusCode) : 500;
                            final org.mule.module.http.internal.domain.response.HttpResponseBuilder failureResponseBuilder = new org.mule.module.http.internal.domain.response.HttpResponseBuilder()
                                    .setStatusCode(statusCodeFromException)
                                    .setReasonPhrase(messagingException.getMessage());
                            addThrottlingHeaders(failureResponseBuilder);
                            MuleEvent event = messagingException.getEvent();
                            event.getMessage().setPayload(exception.getMessage());
                            final HttpResponse response;
                            try
                            {
                                response = errorResponseBuilder.build(failureResponseBuilder, event);
                            }
                            catch (MessagingException e)
                            {
                                throw new MuleRuntimeException(e);
                            }

                            responseCallback.responseReady(response, getResponseFailureCallback(messagingException.getEvent()));
                        }
                    };
                    sourceContext.getMessageHandler().handle(createEvent(requestContext).getMessage().asNewMessage(), httpCompletionHandler);
                //    final HttpMessageProcessorTemplate httpMessageProcessorTemplate = new HttpMessageProcessorTemplate(createEvent(requestContext), messageProcessor, responseCallback, responseBuilder, errorResponseBuilder);
                //    final HttpMessageProcessContext messageProcessContext = new HttpMessageProcessContext(HttpListener.this, flowConstruct, config.getWorkManager(), muleContext.getExecutionClassLoader());
                //    messageProcessingManager.processMessage(httpMessageProcessorTemplate, messageProcessContext);
                }
                catch (HttpRequestParsingException | IllegalArgumentException e)
                {
                    logger.warn("Exception occurred parsing request:", e);
                    sendErrorResponse(BAD_REQUEST, e.getMessage(), responseCallback);
                }
                catch (RuntimeException e)
                {
                    logger.warn("Exception occurred processing request:", e);
                    sendErrorResponse(INTERNAL_SERVER_ERROR, SERVER_PROBLEM, responseCallback);
                }
                finally
                {
                    RequestContext.clear();
                }
            }

            private void sendErrorResponse(final HttpStatus status, String message, HttpResponseReadyCallback responseCallback)
            {
                responseCallback.responseReady(new org.mule.module.http.internal.domain.response.HttpResponseBuilder()
                                                       .setStatusCode(status.getStatusCode())
                                                       .setReasonPhrase(status.getReasonPhrase())
                                                       .setEntity(new ByteArrayHttpEntity(message.getBytes()))
                                                       .build(), new ResponseStatusCallback()
                {
                    @Override
                    public void responseSendFailure(Throwable exception)
                    {
                        logger.warn("Error while sending {} response {}", status.getStatusCode(), exception.getMessage());
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Exception thrown", exception);
                        }
                    }

                    @Override
                    public void responseSendSuccessfully()
                    {
                    }
                });
            }
        };
    }

    private MuleEvent createEvent(HttpRequestContext requestContext) throws HttpRequestParsingException
    {
        MuleEvent muleEvent = HttpRequestToMuleEvent.transform(requestContext, muleContext, flowConstruct, parseRequest, listenerPath);
        // Update RequestContext ThreadLocal for backwards compatibility
        OptimizedRequestContext.unsafeSetEvent(muleEvent);
        return muleEvent;
    }

    @Override
    public synchronized void initialise() throws InitialisationException
    {
        if (allowedMethods != null)
        {
            parsedAllowedMethods = extractAllowedMethods();
            methodRequestMatcher = new MethodRequestMatcher(parsedAllowedMethods);
        }
        if (responseBuilder == null)
        {
            responseBuilder = HttpResponseBuilder.emptyInstance(muleContext);
        }

        LifecycleUtils.initialiseIfNeeded(responseBuilder);

        if (errorResponseBuilder == null)
        {
            errorResponseBuilder = HttpResponseBuilder.emptyInstance(muleContext);
        }

        LifecycleUtils.initialiseIfNeeded(errorResponseBuilder);

        path = HttpParser.sanitizePathWithStartSlash(path);
        HttpListenerConfig listenerConfig = (HttpListenerConfig) config;
        listenerPath = listenerConfig.getFullListenerPath(path);
        path = listenerPath.getResolvedPath();
        responseBuilder.setResponseStreaming(responseStreamingMode);
        validatePath();
        parseRequest = listenerConfig.resolveParseRequest(parseRequest);
        try
        {
            messageProcessingManager = HttpListener.this.muleContext.getRegistry().lookupObject(MessageProcessingManager.class);
            requestHandlerManager = listenerConfig.addRequestHandler(new ListenerRequestMatcher(methodRequestMatcher, path), getRequestHandler());
        }
        catch (Exception e)
        {
            throw new InitialisationException(e, this);
        }
    }

    private void validatePath() throws InitialisationException
    {
        final String[] pathParts = this.path.split("/");
        List<String> uriParamNames = new ArrayList<>();
        for (String pathPart : pathParts)
        {
            if (pathPart.startsWith("{") && pathPart.endsWith("}"))
            {
                String uriParamName = pathPart.substring(1, pathPart.length() - 1);
                if (uriParamNames.contains(uriParamName))
                {
                    throw new InitialisationException(CoreMessages.createStaticMessage(String.format("Http Listener with path %s contains duplicated uri param names", this.path)), this);
                }
                uriParamNames.add(uriParamName);
            }
            else
            {
                if (pathPart.contains("*") && pathPart.length() > 1)
                {
                    throw new InitialisationException(CoreMessages.createStaticMessage(String.format("Http Listener with path %s contains an invalid use of a wildcard. Wildcards can only be used at the end of the path (i.e.: /path/*) or between / characters (.i.e.: /path/*/anotherPath))", this.path)), this);
                }
            }
        }
    }

    private String[] extractAllowedMethods() throws InitialisationException
    {
        final String[] values = this.allowedMethods.split(",");
        final String[] normalizedValues = new String[values.length];
        int normalizedValueIndex = 0;
        for (String value : values)
        {
            normalizedValues[normalizedValueIndex] = value.trim().toUpperCase();
            normalizedValueIndex++;
        }
        return normalizedValues;
    }

    protected HttpResponse buildResponse(org.mule.api.MuleMessage muleEvent, final org.mule.module.http.internal.domain.response.HttpResponseBuilder responseBuilder)
    {
        MuleEvent event = new DefaultMuleEvent(muleEvent, flowConstruct);
        try
        {
            addThrottlingHeaders(responseBuilder);
            final HttpResponse httpResponse;

            if (muleEvent == null)
            {
                // If the event was filtered, return an empty response with status code 200 OK.
                httpResponse = responseBuilder.setStatusCode(200).build();
            }
            else
            {
                httpResponse = this.responseBuilder.build(responseBuilder, event);
            }
            return httpResponse;
        }
        catch (Exception e)
        {
            // Handle errors that occur while building the response
            return responseBuilder.setStatusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                    .setReasonPhrase(INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .build();
        }
    }


    private ResponseStatusCallback getResponseFailureCallback(final MuleEvent muleEvent)
    {
        return new ResponseStatusCallback()
        {
            @Override
            public void responseSendFailure(Throwable throwable)
            {
                sourceContext.getExceptionCallback().onException(getException(throwable));
                //responseCompletationCallback.responseSentWithFailure(getException(throwable), muleEvent);
            }

            @Override
            public void responseSendSuccessfully()
            {

            }
        };
    }

    private Exception getException(Throwable throwable)
    {
        if (throwable instanceof Exception)
        {
            return (Exception) throwable;
        }
        return new Exception(throwable);
    }

    private void addThrottlingHeaders(org.mule.module.http.internal.domain.response.HttpResponseBuilder throttledResponseBuilder)
    {
        final Map<String, String> throttlingHeaders = getThrottlingHeaders();
        for (String throttlingHeaderName : throttlingHeaders.keySet())
        {
            throttledResponseBuilder.addHeader(throttlingHeaderName, throttlingHeaders.get(throttlingHeaderName));
        }
    }

    private Map<String,String> getThrottlingHeaders()
    {
        return httpThrottlingHeadersMapBuilder.build();
    }

    @Override
    public void setMuleContext(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

    @Override
    public void setFlowConstruct(FlowConstruct flowConstruct)
    {
        this.flowConstruct = flowConstruct;
    }

    @Override
    public synchronized void stop()
    {
        requestHandlerManager.stop();
    }

    @Override
    public void dispose()
    {
        requestHandlerManager.dispose();
    }

    public String getPath()
    {
        return path;
    }

    public String[] getAllowedMethods()
    {
        return parsedAllowedMethods;
    }
}
