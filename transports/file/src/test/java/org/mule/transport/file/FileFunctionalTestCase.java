/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.file;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.transport.file.FileConnector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * We are careful here to access the file sstem in a generic way.  This means setting directories
 * dynamically.
 */
public class FileFunctionalTestCase extends AbstractFileFunctionalTestCase
{

    public void testSend() throws Exception
    {
        File target = File.createTempFile("mule-file-test-", ".txt");
        target.deleteOnExit();

        FileConnector connector =
                (FileConnector) muleContext.getRegistry().lookupConnector("sendConnector");
        connector.setWriteToDirectory(target.getParent());
        logger.debug("Directory is " + connector.getWriteToDirectory());
        Map props = new HashMap();
        props.put(TARGET_FILE, target.getName());
        logger.debug("File is " + props.get(TARGET_FILE));

        MuleClient client = new MuleClient();
        client.dispatch("send", TEST_MESSAGE, props);
        waitForFileSystem();

        String result = new BufferedReader(new FileReader(target)).readLine();
        assertEquals(TEST_MESSAGE, result);
    }

    public void testDirectRequest() throws Exception
    {
        File target = initForRequest();
        MuleClient client = new MuleClient();
        String url = fileToUrl(target) + "?connector=receiveConnector";
        logger.debug(url);
        MuleMessage message = client.request(url, 100000);
        checkReceivedMessage(message);
    }
    
    public void testRecursive() throws Exception
    {
        File directory = new File("./.mule/in");
        File subDirectory = new File(directory.getAbsolutePath() + "/sub");
        boolean success = subDirectory.mkdir();
        assertTrue(success);
        subDirectory.deleteOnExit();

        File target = File.createTempFile("mule-file-test-", ".txt", subDirectory);
        Writer out = new FileWriter(target);
        out.write(TEST_MESSAGE);
        out.close();
        target.deleteOnExit();

        MuleClient client = new MuleClient();
        Thread.sleep(1000);
        MuleMessage message = client.request("vm://receive?connector=vmQueue", 100000);
        assertEquals(TEST_MESSAGE, message.getPayloadAsString());
    }
}
