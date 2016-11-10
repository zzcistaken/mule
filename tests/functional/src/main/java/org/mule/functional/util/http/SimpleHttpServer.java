/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.functional.util.http;

import static org.apache.commons.lang.ArrayUtils.toObject;
import org.mule.runtime.core.util.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.LinkedList;

public class SimpleHttpServer {

  private final int port;
  private HttpServer server;
  private LinkedList<Byte[]> httpRequests = new LinkedList<>();

  public Byte[] getLastHttpRequestBody() {
    return httpRequests.getLast();
  }

  private SimpleHttpServer(int port) {
    this.port = port;
  }

  public static SimpleHttpServer createServer(int port) {
    SimpleHttpServer simpleHttpServer = new SimpleHttpServer(port);
    return simpleHttpServer;
  }

  public SimpleHttpServer start() {
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/", new TestHandler());
      server.setExecutor(null); // creates a default executor
      server.start();
      return this;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void stop() {
    try {
      server.stop(0);
    } catch (Exception e) {
      // nothing to do.
    }
  }

  class TestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {
      httpRequests.push(toObject(IOUtils.toByteArray(t.getRequestBody())));
      String response = "This is the response";
      t.sendResponseHeaders(200, response.length());
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }


}
