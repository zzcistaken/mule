/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.deployer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.osgi.service.url.AbstractURLStreamHandlerService;

/**
 *
 */
public class MabURLHandler extends AbstractURLStreamHandlerService
{

    @Override
    public URLConnection openConnection(URL url) throws IOException {
        if (url.getPath() == null || url.getPath().trim().length() == 0) {
            //TODO(pablo.kraan): OSGi - add expected suntax
            throw new MalformedURLException("Path cannot be null or empty");
        }

        //logger.debug("Blueprint xml URL is: [" + url.getPath() + "]");
        return new MabConnection(url);
    }

}
