/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.deployer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

/**
 *
 */
public class MabArtifactInstallerTestCase
{

    private MabArtifactInstaller artifactInstaller = new MabArtifactInstaller();

    @Test
    public void doesNotHandleMabFileWithoutConfig() throws Exception
    {
        File file = new File("app.mab");

        assertThat(artifactInstaller.canHandle(file), is(true));
    }
}
