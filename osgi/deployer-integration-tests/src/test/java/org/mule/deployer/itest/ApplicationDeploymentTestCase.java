/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer.itest;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import org.mule.config.StartupContext;
import org.mule.deployer.api.ApplicationBundle;
import org.mule.deployer.application.ApplicationStatus;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ApplicationDeploymentTestCase extends AbstractDeployerFunctionalTestCase
{
    //TODO(pablo.kraan): OSGi - add a test for deployment an app with java classes
    //TODO(pablo.kraan): OSGi - add a test for deployment an app with libraries
    //TODO(pablo.kraan): OSGi - add a test for deployment an app with plugins

    @Test
    public void deploysAppZipOnStartup() throws Exception
    {
        final List<ApplicationBundle> applications = deploymentService.getApplications();
        assertThat(applications, empty());
        addPackedAppFromResource(emptyAppDescriptor.zipPath);

        deploymentService.start();

        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);
        assertAppsDir(NONE, new String[] {emptyAppDescriptor.id}, true);
        assertApplicationAnchorFileExists(emptyAppDescriptor.id);

        //TODO(pablo.kraan): OSGi - check if we still need to access app mule context and stuff
        //// just assert no privileged entries were put in the registry
        //final Application app = findApp(emptyAppDescriptor.id, 1);
        //final MuleRegistry registry = getMuleRegistry(app);
        //
        //// mule-app.properties from the zip archive must have loaded properly
        //assertEquals("mule-app.properties should have been loaded.", "someValue", registry.get("myCustomProp"));
    }

    @Test
    public void deploysExplodedAppOnStartup() throws Exception
    {
        addExplodedAppFromResource(emptyAppDescriptor.zipPath);

        deploymentService.start();

        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);
        assertAppsDir(NONE, new String[] {emptyAppDescriptor.id}, true);
        assertApplicationAnchorFileExists(emptyAppDescriptor.id);
    }

    @Test
    public void undeploysApplicationRemovingAnchorFile() throws Exception
    {
        addPackedAppFromResource(emptyAppDescriptor.zipPath);

        deploymentService.start();

        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);
        ApplicationBundle app = findApp(emptyAppDescriptor.id, 1);

        assertTrue("Unable to remove anchor file", removeAppAnchorFile(emptyAppDescriptor.id));

        assertUndeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);
        assertStatus(app, ApplicationStatus.DESTROYED);
    }

    @Test
    public void deploysAppWithContainedLibZipOnStartup() throws Exception
    {
        final List<ApplicationBundle> applications = deploymentService.getApplications();
        assertThat(applications, empty());
        addPackedAppFromResource(containedLibAppDescriptor.zipPath);

        deploymentService.start();

        assertApplicationDeploymentSuccess(applicationDeploymentListener, containedLibAppDescriptor.id);
        assertAppsDir(NONE, new String[] {containedLibAppDescriptor.id}, true);
        assertApplicationAnchorFileExists(containedLibAppDescriptor.id);
                          Thread.sleep(15000);
        //TODO(pablo.kraan): OSGi - check if we still need to access app mule context and stuff
        //// just assert no privileged entries were put in the registry
        //final Application app = findApp(containedLibAppDescriptor.id, 1);
        //final MuleRegistry registry = getMuleRegistry(app);
        //
        //// mule-app.properties from the zip archive must have loaded properly
        //assertEquals("mule-app.properties should have been loaded.", "someValue", registry.get("myCustomProp"));
    }

    @Test
    public void mantainsAppFolderOnExplodedAppDeploymentError() throws Exception
    {
        deploymentService.start();

        addPackedAppFromResource(incompleteAppDescriptor.zipPath);

        assertDeploymentFailure(applicationDeploymentListener, incompleteAppDescriptor.id);

        // Deploys another app to confirm that DeploymentService has execute the updater thread
        addPackedAppFromResource(emptyAppDescriptor.zipPath);

        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);

        // Check that the failed application folder is still there
        assertAppFolderIsMaintained(incompleteAppDescriptor.id);
        final Map.Entry<URL, Long> zombie = getZombieApplications().entrySet().iterator().next();
        assertEquals("Wrong URL tagged as zombie.", incompleteAppDescriptor.id, new File(zombie.getKey().getFile()).getParentFile().getName());
    }

    @Test
    public void deploysIncompleteZipAppOnStartup() throws Exception
    {
        addPackedAppFromResource(incompleteAppDescriptor.zipPath);

        deploymentService.start();

        assertDeploymentFailure(applicationDeploymentListener, incompleteAppDescriptor.id);

        // Deploys another app to confirm that DeploymentService has execute the updater thread
        addPackedAppFromResource(emptyAppDescriptor.zipPath);

        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);

        // Check that the failed application folder is still there
        assertAppFolderIsMaintained(incompleteAppDescriptor.id);
        final Map.Entry<URL, Long> zombie = getZombieApplications().entrySet().iterator().next();
        assertEquals("Wrong URL tagged as zombie.", incompleteAppDescriptor.id, new File(zombie.getKey().getFile()).getParentFile().getName());
    }

    @Test
    public void deploysIncompleteZipAppAfterStartup() throws Exception
    {
        deploymentService.start();

        addPackedAppFromResource(incompleteAppDescriptor.zipPath);

        assertDeploymentFailure(applicationDeploymentListener, incompleteAppDescriptor.id);

        // Deploys another app to confirm that DeploymentService has execute the updater thread
        addPackedAppFromResource(emptyAppDescriptor.zipPath);

        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);

        // Check that the failed application folder is still there
        assertAppFolderIsMaintained(incompleteAppDescriptor.id);
        final Map.Entry<URL, Long> zombie = getZombieApplications().entrySet().iterator().next();
        assertEquals("Wrong URL tagged as zombie.", incompleteAppDescriptor.id, new File(zombie.getKey().getFile()).getParentFile().getName());
    }



    @Test
    public void synchronizesDeploymentOnStart() throws Exception
    {
        addPackedAppFromResource(emptyAppDescriptor.zipPath);

        Thread deploymentServiceThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                deploymentService.start();
            }
        });

        final boolean[] lockedFromClient = new boolean[] {false};

        Mockito.doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {

                Thread deploymentClientThread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ReentrantLock deploymentLock = deploymentService.getLock();

                        try
                        {
                            try
                            {
                                lockedFromClient[0] = deploymentLock.tryLock(1000, TimeUnit.MILLISECONDS);
                            }
                            catch (InterruptedException e)
                            {
                                // Ignore
                            }
                        }
                        finally
                        {
                            if (deploymentLock.isHeldByCurrentThread())
                            {
                                deploymentLock.unlock();
                            }
                        }
                    }
                });

                deploymentClientThread.start();
                deploymentClientThread.join();

                return null;
            }
        }).when(applicationDeploymentListener).onDeploymentStart(emptyAppDescriptor.id);

        deploymentServiceThread.start();

        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);
        assertThat("Able to lock deployment service during start", lockedFromClient[0], is(false));
    }

    @Test
    public void deploysExplodedAppAndVerifyAnchorFileIsCreatedAfterDeploymentEnds() throws Exception
    {
        Action deployExplodedWaitAppAction = new Action()
        {
            @Override
            public void perform() throws Exception
            {
                addExplodedAppFromResource(waitAppDescriptor.zipPath);
            }
        };
        deploysAppAndVerifyAnchorFileIsCreatedAfterDeploymentEnds(deployExplodedWaitAppAction);
    }

    @Test
    public void deploysPackagedAppAndVerifyAnchorFileIsCreatedAfterDeploymentEnds() throws Exception
    {
        Action deployPackagedWaitAppAction = new Action()
        {
            @Override
            public void perform() throws Exception
            {
                addPackedAppFromResource(waitAppDescriptor.zipPath);
            }
        };
        deploysAppAndVerifyAnchorFileIsCreatedAfterDeploymentEnds(deployPackagedWaitAppAction);
    }

    @Test
    public void deploysAppZipAfterStartup() throws Exception
    {
        deploymentService.start();

        addPackedAppFromResource(emptyAppDescriptor.zipPath);

        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);
        assertAppsDir(NONE, new String[] {emptyAppDescriptor.id}, true);
        assertApplicationAnchorFileExists(emptyAppDescriptor.id);
    }

    @Test
    public void deploysBrokenAppZipOnStartup() throws Exception
    {
        addPackedAppFromResource(brokenAppDescriptor.zipPath, brokenAppDescriptor.targetPath);

        deploymentService.start();

        assertDeploymentFailure(applicationDeploymentListener, "brokenApp");

        assertAppsDir(new String[] {"brokenApp.zip"}, NONE, true);

        assertApplicationAnchorFileDoesNotExists(brokenAppDescriptor.id);

        final Map<URL, Long> zombieMap = getZombieApplications();
        assertEquals("Wrong number of zombie apps registered.", 1, zombieMap.size());
        final Map.Entry<URL, Long> zombie = zombieMap.entrySet().iterator().next();
        assertEquals("Wrong URL tagged as zombie.", "brokenApp.zip", new File(zombie.getKey().getFile()).getName());
        assertTrue("Invalid lastModified value for file URL.", zombie.getValue() != -1);
    }

    /**
     * This tests deploys a broken app which name has a weird character.
     * It verifies that after failing deploying that app, it doesn't try to do it
     * again, which is a behavior than can be seen in some file systems due to
     * path handling issues
     */
    @Test
    public void dontRetryBrokenAppWithFunkyName() throws Exception
    {
        addPackedAppFromResource(brokenAppWithFunkyNameDescriptor.zipPath, brokenAppWithFunkyNameDescriptor.targetPath);

        deploymentService.start();

        assertDeploymentFailure(applicationDeploymentListener, "brokenApp+");

        assertAppsDir(new String[] {"brokenApp+.zip"}, NONE, true);

        assertApplicationAnchorFileDoesNotExists(brokenAppDescriptor.id);

        final Map<URL, Long> zombieMap = getZombieApplications();
        assertEquals("Wrong number of zombie apps registered.", 1, zombieMap.size());
        final Map.Entry<URL, Long> zombie = zombieMap.entrySet().iterator().next();
        assertEquals("Wrong URL tagged as zombie.", "brokenApp+.zip", new File(zombie.getKey().getFile()).getName());
        assertTrue("Invalid lastModified value for file URL.", zombie.getValue() != -1);

        reset(applicationDeploymentListener);

        addPackedAppFromResource(emptyAppDescriptor.zipPath);

        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);
        assertDeploymentFailure(applicationDeploymentListener, "brokenApp+", never());

        addPackedAppFromResource(emptyAppDescriptor.zipPath);
        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);
        assertDeploymentFailure(applicationDeploymentListener, "brokenApp+", never());
    }

    @Test
    public void deploysBrokenAppZipAfterStartup() throws Exception
    {
        deploymentService.start();

        addPackedAppFromResource(brokenAppDescriptor.zipPath, "brokenApp.zip");

        assertDeploymentFailure(applicationDeploymentListener, "brokenApp");

        assertAppsDir(new String[] {"brokenApp.zip"}, NONE, true);

        assertApplicationAnchorFileDoesNotExists(brokenAppDescriptor.id);

        final Map<URL, Long> zombieMap = getZombieApplications();
        assertEquals("Wrong number of zombie apps registered.", 1, zombieMap.size());
        final Map.Entry<URL, Long> zombie = zombieMap.entrySet().iterator().next();
        assertEquals("Wrong URL tagged as zombie.", "brokenApp.zip", new File(zombie.getKey().getFile()).getName());
        assertTrue("Invalid lastModified value for file URL.", zombie.getValue() != -1);
    }

    @Test
    public void deploysInvalidZipAppOnStartup() throws Exception
    {
        addPackedAppFromResource(emptyAppDescriptor.zipPath, "app with spaces.zip");

        deploymentService.start();
        assertDeploymentFailure(applicationDeploymentListener, "app with spaces");

        // zip stays intact, no app dir created
        assertAppsDir(new String[] {"app with spaces.zip"}, NONE, true);
        final Map<URL, Long> zombieMap = getZombieApplications();
        assertEquals("Wrong number of zombie apps registered.", 1, zombieMap.size());
        final Map.Entry<URL, Long> zombie = zombieMap.entrySet().iterator().next();

        // Spaces are converted to %20 is returned by java file api :/
        String appName = URLDecoder.decode(new File(zombie.getKey().getFile()).getName(), "UTF-8");
        assertEquals("Wrong URL tagged as zombie.", "app with spaces.zip", appName);
        assertTrue("Invalid lastModified value for file URL.", zombie.getValue() != -1);
    }

    @Test
    public void deploysInvalidZipAppAfterStartup() throws Exception
    {
        deploymentService.start();

        addPackedAppFromResource(emptyAppDescriptor.zipPath, "app with spaces.zip");

        assertDeploymentFailure(applicationDeploymentListener, "app with spaces");

        // zip stays intact, no app dir created
        assertAppsDir(new String[] {"app with spaces.zip"}, NONE, true);
        final Map<URL, Long> zombieMap = getZombieApplications();
        assertEquals("Wrong number of zombie apps registered.", 1, zombieMap.size());
        final Map.Entry<URL, Long> zombie = zombieMap.entrySet().iterator().next();

        // Spaces are converted to %20 is returned by java file api :/
        String appName = URLDecoder.decode(new File(zombie.getKey().getFile()).getName(), "UTF-8");
        assertEquals("Wrong URL tagged as zombie.", "app with spaces.zip", appName);
        assertTrue("Invalid lastModified value for file URL.", zombie.getValue() != -1);
    }

    @Test
    public void deployAppNameWithZipSuffix() throws Exception
    {
        addPackedAppFromResource(emptyAppDescriptor.zipPath, "empty-app.zip.zip");

        deploymentService.start();

        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.targetPath);
        reset(applicationDeploymentListener);

        assertAppsDir(NONE, new String[] {emptyAppDescriptor.targetPath}, true);
        assertEquals("Application has not been properly registered with Mule", 1, deploymentService.getApplications().size());

        // Checks that the empty-app.zip folder is not processed as a zip file
        assertNoDeploymentInvoked(applicationDeploymentListener);
    }


    //////////////////////////////////////////////////////////////////////
    // Application deployment using -app argument
    //////////////////////////////////////////////////////////////////////

    @Test
    public void deploysPackedAppsInOrderWhenAppArgumentIsUsed() throws Exception
    {
        addPackedAppFromResource(emptyAppDescriptor.zipPath, "app1.zip");
        addPackedAppFromResource(emptyAppDescriptor.zipPath, "app2.zip");
        addPackedAppFromResource(emptyAppDescriptor.zipPath, "app3.zip");

        Map<String, Object> startupOptions = new HashMap<String, Object>();
        startupOptions.put("app", "app3:app1:app2");
        StartupContext.get().setStartupOptions(startupOptions);

        deploymentService.start();

        assertApplicationDeploymentSuccess(applicationDeploymentListener, "app1");
        assertApplicationDeploymentSuccess(applicationDeploymentListener, "app2");
        assertApplicationDeploymentSuccess(applicationDeploymentListener, "app3");
        assertAppsDir(NONE, new String[] {"app1", "app2", "app3"}, true);

        // When apps are passed as -app app1:app2:app3 the startup order matters
        List<ApplicationBundle> applications = deploymentService.getApplications();
        assertNotNull(applications);
        assertEquals(3, applications.size());
        assertEquals("app3", applications.get(0).getArtifactName());
        assertEquals("app1", applications.get(1).getArtifactName());
        assertEquals("app2", applications.get(2).getArtifactName());
    }

    @Test
    public void deploysExplodedAppsInOrderWhenAppArgumentIsUsed() throws Exception
    {
        addExplodedAppFromResource(emptyAppDescriptor.zipPath, "app1");
        addExplodedAppFromResource(emptyAppDescriptor.zipPath, "app2");
        addExplodedAppFromResource(emptyAppDescriptor.zipPath, "app3");

        Map<String, Object> startupOptions = new HashMap<String, Object>();
        startupOptions.put("app", "app3:app1:app2");
        StartupContext.get().setStartupOptions(startupOptions);

        deploymentService.start();

        assertApplicationDeploymentSuccess(applicationDeploymentListener, "app1");
        assertApplicationDeploymentSuccess(applicationDeploymentListener, "app2");
        assertApplicationDeploymentSuccess(applicationDeploymentListener, "app3");

        assertAppsDir(NONE, new String[] {"app1", "app2", "app3"}, true);

        // When apps are passed as -app app1:app2:app3 the startup order matters
        List<ApplicationBundle> applications = deploymentService.getApplications();
        assertNotNull(applications);
        assertEquals(3, applications.size());
        assertEquals("app3", applications.get(0).getArtifactName());
        assertEquals("app1", applications.get(1).getArtifactName());
        assertEquals("app2", applications.get(2).getArtifactName());
    }

    @Test
    public void deploysAppJustOnce() throws Exception
    {
        addPackedAppFromResource(emptyAppDescriptor.zipPath);

        Map<String, Object> startupOptions = new HashMap<String, Object>();
        startupOptions.put("app", "empty-app:empty-app:empty-app");
        StartupContext.get().setStartupOptions(startupOptions);

        deploymentService.start();

        assertApplicationDeploymentSuccess(applicationDeploymentListener, emptyAppDescriptor.id);
        assertAppsDir(NONE, new String[] {emptyAppDescriptor.id}, true);

        List<ApplicationBundle> applications = deploymentService.getApplications();
        assertEquals(1, applications.size());
    }

    @Test
    public void brokenAppArchiveWithoutArgument() throws Exception
    {
        doBrokenAppArchiveTest();
    }

    @Test
    public void brokenAppArchiveAsArgument() throws Exception
    {
        Map<String, Object> startupOptions = new HashMap<String, Object>();
        startupOptions.put("app", brokenAppDescriptor.id);
        StartupContext.get().setStartupOptions(startupOptions);

        doBrokenAppArchiveTest();
    }

    public void doBrokenAppArchiveTest() throws Exception
    {
        addPackedAppFromResource(brokenAppDescriptor.zipPath);

        deploymentService.start();

        assertDeploymentFailure(applicationDeploymentListener, brokenAppDescriptor.id);
        reset(applicationDeploymentListener);

        // let the file system's write-behind cache commit the delete operation?
        Thread.sleep(FILE_TIMESTAMP_PRECISION_MILLIS);

        // zip stays intact, no app dir created
        assertAppsDir(new String[] {"broken-app.zip"}, NONE, true);
        // don't assert dir contents, we want to check internal deployer state next
        assertAppsDir(NONE, new String[] {emptyAppDescriptor.id}, false);
        assertEquals("No apps should have been registered with Mule.", 0, deploymentService.getApplications().size());
        final Map<URL, Long> zombieMap = getZombieApplications();
        assertEquals("Wrong number of zombie apps registered.", 1, zombieMap.size());
        final Map.Entry<URL, Long> zombie = zombieMap.entrySet().iterator().next();
        assertEquals("Wrong URL tagged as zombie.", "broken-app.zip", new File(zombie.getKey().getFile()).getName());
        assertTrue("Invalid lastModified value for file URL.", zombie.getValue() != -1);

        // Checks that the invalid zip was not deployed again
        try
        {
            assertDeploymentFailure(applicationDeploymentListener, "broken-app.zip");
            fail("Install was invoked again for the broken application file");
        }
        catch (AssertionError expected)
        {
        }
    }

}
