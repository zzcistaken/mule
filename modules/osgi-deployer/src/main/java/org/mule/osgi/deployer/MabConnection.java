/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.deployer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
*
*/
public class MabConnection extends URLConnection
{

    public MabConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException
    {
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            //TODO(pablo.kraan): OSGi - add transformer
            //BlueprintTransformer.transform(new URL(url.getPath()), os);
            os.close();
            return new ByteArrayInputStream(os.toByteArray());
        } catch (Exception e) {
            //logger.error("Error opening blueprint xml url", e);
            throw (IOException) new IOException("Error opening mule application bundle url").initCause(e);
        }
    }
}
