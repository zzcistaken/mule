/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.spring.editors;

import org.mule.config.i18n.CoreMessages;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Flags;

/**
 * A property editor for {@link Flags.Flag}
 */
public class FlagPropertyEditor extends PropertyEditorSupport
{

    private static Map<String, Flags.Flag> STRING_TO_FLAG;

    static
    {
        Map<String, Flags.Flag> mapping = new HashMap<String, Flags.Flag>();
        mapping.put("ANSWERED", Flags.Flag.ANSWERED);
        mapping.put("DELETED", Flags.Flag.DELETED);
        mapping.put("DRAFT", Flags.Flag.DRAFT);
        mapping.put("FLAGGED", Flags.Flag.FLAGGED);
        mapping.put("RECENT", Flags.Flag.RECENT);
        mapping.put("SEEN", Flags.Flag.SEEN);
        mapping.put("USER", Flags.Flag.USER);
        mapping.put("NONE", null);
        STRING_TO_FLAG = mapping;
    };

    @Override
    public void setAsText(String text)
    {
        if (STRING_TO_FLAG.containsKey(text))
        {
            setValue(STRING_TO_FLAG.get(text));
        }
        else
        {
            throw new IllegalArgumentException(CoreMessages.failedToCreate("Flags.Flag").getMessage());
        }
    }
}
