/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule;

import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.CoreMessages;
import org.mule.transformer.AbstractTransformer;

/**
 * @author estebanroblesluna
 * @since 2.2.6
 */
public class IncButEvenFailingTransformer extends AbstractTransformer
{
    @Override
    protected Object doTransform(Object src, String encoding) throws TransformerException
    {
        if (((Integer) src) % 2 == 0)
        {
            throw new TransformerException(CoreMessages.failedToReadPayload(), this);
        }
        else
        {
            return ((Integer) src) + 1;
        }
    }
}
