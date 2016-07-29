/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.functional.classloading.isolation.maven;

import java.util.List;

/**
 * Provides a dependency graph in .dot format
 *
 * @since 4.0
 */
public interface RawDependencyGraphProvider
{

    /**
     * @return a List of string representing the lines of the dependency graph in .dot format
     */
    List<String> getDependencyGraph();

}
