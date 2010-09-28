/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport;

import org.mule.RequestContext;
import org.mule.api.MuleEvent;
import org.mule.tck.AbstractMuleTestCase;

public class MessagePropertiesContextTestCase extends AbstractMuleTestCase
{

    public void testSessionScope() throws Exception
    {
        MuleEvent e = getTestEvent("testing");
        e.getSession().setProperty("SESSION_PROP", "Value1");
        RequestContext.setEvent(e);

        MessagePropertiesContext mpc = new MessagePropertiesContext();
        MessagePropertiesContext mpcCopy = mpc.copy();
        
        //access inst var for testing purposes
        assertTrue(mpc.keySet.isEmpty());
        assertTrue(mpcCopy.keySet.isEmpty());
    }
}