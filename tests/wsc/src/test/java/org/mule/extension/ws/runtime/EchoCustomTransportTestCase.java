/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.ws.runtime;

import static org.mule.extension.ws.WscTestUtils.ECHO;
import static org.mule.extension.ws.WscTestUtils.HEADER_IN;
import static org.mule.extension.ws.WscTestUtils.HEADER_INOUT;
import static org.mule.extension.ws.WscTestUtils.assertSoapResponse;
import static org.mule.extension.ws.WscTestUtils.getRequestResource;
import org.mule.extension.ws.AbstractSoapServiceTestCase;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.util.IOUtils;

import org.junit.Test;
import ru.yandex.qatools.allure.annotations.Description;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

@Features("Web Service Consumer")
@Stories("Operation Execution")
public class EchoCustomTransportTestCase extends AbstractSoapServiceTestCase {

  // Flow Names
  private static final String ECHO_FLOW = "echoOperation";

  @Override
  protected String getConfigurationFile() {
    return "config/echoWithCustomTransport.xml";
  }

  @Test
  @Description("Consumes an operation that expects a simple type and returns a simple type")
  public void echoOperation() throws Exception {
    Message message = runFlowWithRequest(ECHO_FLOW, ECHO);
    String out = (String) message.getPayload().getValue();
    assertSoapResponse(ECHO, out);
  }

  protected Message runFlowWithRequest(String flowName, String requestXmlResourceName) throws Exception {
    return flowRunner(flowName)
        .withPayload(getRequestResource(requestXmlResourceName))
        .withVariable(HEADER_IN, getRequestResource(HEADER_IN))
        .withVariable(HEADER_INOUT, getRequestResource(HEADER_INOUT))
        .withVariable("servicePort", servicePort.getValue())
        .withVariable("destination", "ble")
        .withVariable("echo", IOUtils.toInputStream(ECHO))
        .run()
        .getMessage();
  }
}
