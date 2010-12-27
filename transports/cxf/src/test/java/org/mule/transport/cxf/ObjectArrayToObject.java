/*
 * $Id$
 *
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.cxf;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

public class ObjectArrayToObject extends AbstractMessageAwareTransformer
{

    @Override
    public Object transform(MuleMessage arg0, String arg1)
            throws TransformerException
    {
        if (arg0.getPayload() instanceof Object[])
        {
            Object[] p = (Object[]) arg0.getPayload();
            arg0.setPayload(p[p.length - 1]);
        }
        return arg0;
    }
}
