/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.vegan;

import org.mule.extension.api.annotation.param.Connection;
import org.mule.tck.testmodels.fruit.Kiwi;

public class EatKiwiOperation
{

    public Kiwi eatKiwi(@Connection  Kiwi kiwi)
    {
        kiwi.bite();
        return kiwi;
    }
}
