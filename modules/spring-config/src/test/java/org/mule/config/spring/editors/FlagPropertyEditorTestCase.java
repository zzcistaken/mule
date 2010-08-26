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

import org.mule.tck.AbstractMuleTestCase;

import javax.mail.Flags;
import javax.mail.Flags.Flag;

public class FlagPropertyEditorTestCase extends AbstractMuleTestCase
{

    public void testFlagConversion()
    {
        this.basicTestFlagConversion("ANSWERED", Flags.Flag.ANSWERED);
        this.basicTestFlagConversion("DELETED", Flags.Flag.DELETED);
        this.basicTestFlagConversion("DRAFT", Flags.Flag.DRAFT);
        this.basicTestFlagConversion("FLAGGED", Flags.Flag.FLAGGED);
        this.basicTestFlagConversion("RECENT", Flags.Flag.RECENT);
        this.basicTestFlagConversion("SEEN", Flags.Flag.SEEN);
        this.basicTestFlagConversion("USER", Flags.Flag.USER);
    }
    
    public void testNone()
    {
        FlagPropertyEditor editor = new FlagPropertyEditor();
        editor.setAsText("NONE");
        assertNull(editor.getValue());
    }
    
    private void basicTestFlagConversion(String text, Flag flag)
    {
        FlagPropertyEditor editor = new FlagPropertyEditor();
        editor.setAsText(text);
        assertEquals(flag, editor.getValue());
    }
}