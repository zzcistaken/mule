/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer.itest;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mule.api.config.MuleProperties.MULE_HOME_DIRECTORY_PROPERTY;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.frameworkStartLevel;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.streamBundle;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;
import org.mule.deployer.DeploymentDirectoryWatcher;
import org.mule.deployer.api.DeploymentListener;
import org.mule.deployer.MuleDeploymentService;
import org.mule.deployer.api.ApplicationBundle;
import org.mule.deployer.api.DeploymentService;
import org.mule.deployer.api.MuleFoldersUtil;
import org.mule.deployer.application.ApplicationStatus;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.probe.JUnitProbe;
import org.mule.tck.probe.PollingProber;
import org.mule.tck.probe.Probe;
import org.mule.tck.probe.Prober;
import org.mule.tck.probe.file.FileExists;
import org.mule.util.CollectionUtils;
import org.mule.util.FileUtils;
import org.mule.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.verification.VerificationMode;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public abstract class AbstractDeployerFunctionalTestCase extends AbstractMuleTestCase
{

    //TODO(pablo.kraan): OSGi remove this constant and configuration duplication
    public static final String STARTUP_BUNDLES_FILE = "startupBundles.properties";

    protected static final int FILE_TIMESTAMP_PRECISION_MILLIS = 1000;
    protected static final int DEPLOYMENT_TIMEOUT = 30000;
    protected static final String[] NONE = new String[0];
    protected static final int ONE_HOUR_IN_MILLISECONDS = 3600000;

    private static final String MULE_CONFIG_XML_FILE = "mule-config.xml";
    private static final String EMPTY_APP_CONFIG_XML = "/empty-config.xml";
    private static final String BAD_APP_CONFIG_XML = "/bad-app-config.xml";
    private static final String PROPERTIES_APP_CONFIG_XML = "/app-properties-config.xml";

    //APP constants
    protected static final ArtifactDescriptor dummyAppDescriptor = new ArtifactDescriptor("dummy-app", "/dummy-app.zip", "/dummy-app", null, null);
    protected static final ArtifactDescriptor emptyAppDescriptor = new ArtifactDescriptor("empty-app", "/empty-app.zip", null, "empty-app.zip", null);
    protected static final ArtifactDescriptor brokenAppDescriptor = new ArtifactDescriptor("broken-app", "/broken-app.zip", null, "brokenApp.zip", null);
    protected static final ArtifactDescriptor brokenAppWithFunkyNameDescriptor = new ArtifactDescriptor("broken-app+", "/broken-app+.zip", null, "brokenApp+.zip", null);
    protected static final ArtifactDescriptor incompleteAppDescriptor = new ArtifactDescriptor("incompleteApp", "/incompleteApp.zip", "/incompleteApp", "incompleteApp.zip", null);
    protected static final ArtifactDescriptor waitAppDescriptor = new ArtifactDescriptor("wait-app", "/wait-app.zip", "/wait-app", "wait-app.zip", "mule-config.xml");
    protected static final ArtifactDescriptor sharedPluginLibAppDescriptor = new ArtifactDescriptor("shared-plugin-lib-app", "/shared-plugin-lib-app.zip", "/shared-plugin-lib-app", "shared-plugin-lib-app.zip", "mule-config.xml");
    protected static final ArtifactDescriptor containedLibAppDescriptor = new ArtifactDescriptor("app-contained-lib", "/app-contained-lib.zip", null, "app-contained-lib.zip", null);
    private static String originalMuleHome;

    protected DeploymentListener applicationDeploymentListener = mock(DeploymentListener.class);

    @Inject
    public DeploymentService deploymentService;

    @Inject
    public BundleContext bundleContext;
    private ServiceRegistration<DeploymentListener> registeredApplicationDeploymentListener;


    @ProbeBuilder
    public TestProbeBuilder build(TestProbeBuilder builder)
    {
        builder.setHeader(Constants.BUNDLE_NAME, this.getClass().getSimpleName() + System.identityHashCode(this));

        // Exports tests package to expose inner classes used on tests
        builder.setHeader(Constants.EXPORT_PACKAGE, this.getClass().getPackage().getName());

        return builder;
    }

    public static class ArtifactDescriptor
    {

        public String id;
        public String zipPath;
        public String path;
        public String targetPath;
        public String configFilePath;

        public ArtifactDescriptor(String id, String zipPath, String path, String targetPath, String configFilePath)
        {
            this.id = id;
            this.zipPath = zipPath;
            this.path = path;
            this.targetPath = targetPath;
            this.configFilePath = configFilePath;
        }
    }

    @Before
    public void setUp() throws Exception
    {
        deploymentService.stop();

        registeredApplicationDeploymentListener = bundleContext.registerService(DeploymentListener.class, applicationDeploymentListener, new Hashtable<String, String>());
    }

    @After
    public void tearDown() throws Exception
    {
        if (registeredApplicationDeploymentListener != null)
        {
            registeredApplicationDeploymentListener.unregister();
        }

        // Cleans up anythings on the shared MULE_HOME folder
        FileUtils.cleanDirectory(MuleFoldersUtil.getAppsFolder());
        FileUtils.cleanDirectory(MuleFoldersUtil.getExecutionFolder());
    }

    @BeforeClass
    public static void getOriginalMuleHome()
    {
        originalMuleHome = System.getProperty(MULE_HOME_DIRECTORY_PROPERTY);
    }
    @AfterClass
    public static void cleanUpMuleHome() throws Exception
    {
        if (StringUtils.isEmpty(originalMuleHome))
        {
            System.clearProperty(MULE_HOME_DIRECTORY_PROPERTY);
        }
        else
        {
            System.setProperty(MULE_HOME_DIRECTORY_PROPERTY, originalMuleHome);
        }
    }

    @Configuration
    public Option[] config()
    {
        System.out.println("Configuring test...");

        setUpFolders();

        return CoreOptions.options(
                systemPackage("sun.misc"),

                ////TODO(pablo.kraan): OSGi - use this dependency instead of the original hamcrest library
                // Dependency added in order to provide non core Hamcrest matchers
                mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.hamcrest", "1.3_1"),

                //TODO(pablo.kraan): OSGi - need to use this dependency instead of the original from mockito (maybe we can update mockito) or use the new version (1.4)
                mavenBundle("org.objenesis", "objenesis", "1.4"),
                mavenBundle("org.mockito", "mockito-core", "1.9.0"),

                getStartupBundles(),

                streamBundle(createTestFeature()).startLevel(70),

                junitBundles(),

                /*
                 * Felix has implicit boot delegation enabled by default. It conflicts with Mockito:
                 * java.lang.LinkageError: loader constraint violation in interface itable initialization:
                 * when resolving method "org.osgi.service.useradmin.User$$EnhancerByMockitoWithCGLIB$$dd2f81dc
                 * .newInstance(Lorg/mockito/cglib/proxy/Callback;)Ljava/lang/Object;" the class loader
                 * (instance of org/mockito/internal/creation/jmock/SearchingClassLoader) of the current class,
                 * org/osgi/service/useradmin/User$$EnhancerByMockitoWithCGLIB$$dd2f81dc, and the class loader
                 * (instance of org/apache/felix/framework/BundleWiringImpl$BundleClassLoaderJava5) for interface
                 * org/mockito/cglib/proxy/Factory have different Class objects for the type org/mockito/cglib/
                 * proxy/Callback used in the signature
                 *
                 * So we disable the bootdelegation. this property has no effect on the other OSGi implementation.
                 */
                frameworkProperty("felix.bootdelegation.implicit").value("false"),

                frameworkStartLevel(100)
        );
    }

    public void setUpFolders()
    {
        try
        {
            final String tmpDir = System.getProperty("java.io.tmpdir");
            File muleHome = new File(new File(tmpDir, "muleHome"), getClass().getSimpleName() + System.currentTimeMillis());
            muleHome.delete();
            System.setProperty(MULE_HOME_DIRECTORY_PROPERTY, muleHome.getCanonicalPath());

            createFolder(MuleFoldersUtil.getMuleHomeFolder());
            createFolder(MuleFoldersUtil.getAppsFolder());
            createFolder(MuleFoldersUtil.getExecutionFolder());
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private void createFolder(File folder)
    {
        if (!folder.mkdirs())
        {
            throw new IllegalStateException(String.format("Unable to create temporal %s folder", folder.getName()));
        }
    }

    private InputStream createTestFeature()
    {
        List<Class> features = getTestFeatures();

        final TinyBundle bundleBuilder = TinyBundles.bundle()
                .set(Constants.BUNDLE_SYMBOLICNAME, "testFeatures");
        bundleBuilder.set( Constants.DYNAMICIMPORT_PACKAGE, "*" );

        StringBuilder featureClassNames = new StringBuilder();
        for (Class feature : features)
        {
            bundleBuilder.add(feature);

            featureClassNames.append(feature.getName()).append("\n");
        }

        bundleBuilder.add("META-INF/features.properties", new ByteArrayInputStream(featureClassNames.toString().getBytes()));

        return bundleBuilder.build();
    }

    protected List<Class> getTestFeatures()
    {
        final ArrayList<Class> features = new ArrayList<>();
        features.add(DeploymentTestFeature.class);
        return features;
    }

    private Option getStartupBundles()
    {
        final Properties properties = new Properties();
        //TODO(pablo.kraan): OSGi - this file must be read from the MULE folder
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(STARTUP_BUNDLES_FILE))
        {
            if (inputStream != null)
            {
                properties.load(inputStream);
            }
        }
        catch (IOException e)
        {
            throw new IllegalStateException(String.format("Unable to open %s file", STARTUP_BUNDLES_FILE), e);
        }

        final List<Option> options = new ArrayList<>();
        for (Object bundleUrl : properties.keySet())
        {
            try
            {
                final int startLevel = Integer.parseInt(properties.getProperty((String) bundleUrl));
                final UrlProvisionOption option = bundle((String) bundleUrl).startLevel(startLevel);

                options.add(option);
            }
            catch (Exception e)
            {
                throw new IllegalStateException("Invalid bundle information format", e);
            }
        }

        return new DefaultCompositeOption(options.toArray(new Option[0]));
    }

    //protected void assertStartedBundles()
    //{
    //    StringBuilder builder = new StringBuilder("Bundle status:\n");
    //    boolean failure = false;
    //    for (Bundle bundle : getBundleContext().getBundles())
    //    {
    //        final boolean isFragment = isFragment(bundle);
    //        if (isFragment && bundle.getState() != Bundle.RESOLVED || !isFragment && bundle.getState() != Bundle.ACTIVE)
    //        {
    //            failure = true;
    //        }
    //        builder.append(bundle.getBundleId() + " ");
    //        builder.append(isFragment ? "Fragment" : "Bundle");
    //        builder.append(" - " + getBundleState(bundle.getState()) + " - " + bundle.getBundleId() + " - " + bundle.getSymbolicName() + " - " + bundle.getVersion() + "\n");
    //    }
    //
    //    System.out.println(builder.toString());
    //
    //    if (failure)
    //    {
    //        fail("There is at least a non active bundle");
    //    }
    //}
    //
    //private static boolean isFragment(Bundle bundle)
    //{
    //    return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    //}
    //
    //private static String getBundleState(int state)
    //{
    //    switch (state)
    //    {
    //        case Bundle.INSTALLED:
    //            return "INSTALLED";
    //        case Bundle.RESOLVED:
    //            return "RESOLVED";
    //        case Bundle.ACTIVE:
    //            return "ACTIVE";
    //        case Bundle.UNINSTALLED:
    //            return "UNINSTALLED";
    //        case Bundle.STARTING:
    //            return "STARTING";
    //        case Bundle.STOPPING:
    //            return "STOPPING";
    //        default:
    //            throw new IllegalStateException("Unknown bundle state: " + state);
    //    }
    //}

    /**
     * Copies a given app archive to the apps folder for deployment.
     */
    protected void addPackedAppFromResource(String resource) throws IOException
    {
        addPackedAppFromResource(resource, null);
    }

    protected void addPackedAppFromResource(String resource, String targetName) throws IOException
    {
        addPackedArtifactFromResource(MuleFoldersUtil.getAppsFolder(), resource, targetName);
    }

    protected void addPackedArtifactFromResource(File targetDir, String resource, String targetName) throws IOException
    {
        URL url = getClass().getResource(resource);
        assertNotNull("Test resource not found: " + url, url);
        addArchive(targetDir, url, targetName);
    }

    private void addArchive(File outputDir ,URL url, String targetFile) throws IOException
    {
        ReentrantLock lock = deploymentService.getLock();

        lock.lock();
        try
        {
            // copy is not atomic, copy to a temp file and rename instead (rename is atomic)
            final String tempFileName = new File((targetFile == null ? url.getFile() : targetFile) + ".part").getName();
            final File tempFile = new File(outputDir, tempFileName);
            FileUtils.copyURLToFile(url, tempFile);
            tempFile.renameTo(new File(StringUtils.removeEnd(tempFile.getAbsolutePath(), ".part")));
        }
        finally
        {
            lock.unlock();
        }
    }

    protected void assertApplicationDeploymentSuccess(DeploymentListener listener, String artifactName)
    {
        assertDeploymentSuccess(listener, artifactName);
        assertStatus(artifactName, ApplicationStatus.STARTED);
    }

    private void assertDeploymentSuccess(final DeploymentListener listener, final String artifactName)
    {
        Prober prober = new PollingProber(DEPLOYMENT_TIMEOUT, 100);
        prober.check(new JUnitProbe()
        {
            @Override
            protected boolean test() throws Exception
            {
                verify(listener, times(1)).onDeploymentSuccess(artifactName);
                return true;
            }

            @Override
            public String describeFailure()
            {
                return "Failed to deploy application: " + artifactName + System.lineSeparator() + super.describeFailure();
            }
        });
    }


    private void assertStatus(String appName, ApplicationStatus status)
    {
        assertStatus(appName, status, -1);
    }

    private void assertStatus(String appName, ApplicationStatus status, int expectedApps)
    {
        ApplicationBundle app = findApp(appName, expectedApps);
        assertThat(app, notNullValue());
        //TODO(pablo.kraan): OSGi - assert application status
        //assertStatus(app, status);
    }

    protected void assertStatus(final ApplicationBundle application, final ApplicationStatus status)
    {
        //TODO(pablo.kraan): OSGi - assert application status
        //Prober prober = new PollingProber(DEPLOYMENT_TIMEOUT, 100);
        //prober.check(new JUnitProbe()
        //{
        //    @Override
        //    protected boolean test() throws Exception
        //    {
        //        assertThat(application.getStatus(), is(status));
        //        return true;
        //    }
        //
        //    @Override
        //    public String describeFailure()
        //    {
        //        return String.format("Application %s was expected to be in status %s but was %s instead",
        //                             application.getArtifactName(), status.name(), application.getStatus().name());
        //    }
        //});
    }

    /**
     * Find a deployed app, performing some basic assertions.
     */
    protected ApplicationBundle findApp(final String appName, int totalAppsExpected)
    {
        // list all apps to validate total count
        final List<ApplicationBundle> applicationBundles = deploymentService.getApplications();
        assertNotNull(applicationBundles);

        if (totalAppsExpected >= 0)
        {
            assertEquals(totalAppsExpected, applicationBundles.size());
        }

        final ApplicationBundle applicationBundle = deploymentService.findApplication(appName);
        assertNotNull(applicationBundle);
        return applicationBundle;
    }

    protected void assertAppsDir(String[] expectedZips, String[] expectedApps, boolean performValidation)
    {
        assertArtifactDir(MuleFoldersUtil.getAppsFolder(), expectedZips, expectedApps, performValidation);
    }

    private void assertArtifactDir(File artifactDir, String[] expectedZips, String[] expectedArtifacts, boolean performValidation)
    {
        final String[] actualZips = artifactDir.list(MuleDeploymentService.ZIP_ARTIFACT_FILTER);
        if (performValidation)
        {
            assertArrayEquals("Invalid Mule artifact archives set", expectedZips, actualZips);
        }
        final String[] actualArtifacts = artifactDir.list(DirectoryFileFilter.DIRECTORY);
        if (performValidation)
        {
            assertTrue("Invalid Mule exploded artifact set",
                       CollectionUtils.isEqualCollection(Arrays.asList(expectedArtifacts), Arrays.asList(actualArtifacts)));
        }
    }

    protected void assertApplicationAnchorFileExists(String applicationName)
    {
        assertThat(getArtifactAnchorFile(applicationName, MuleFoldersUtil.getAppsFolder()).exists(), is(true));
    }

    private File getArtifactAnchorFile(String artifactName, File artifactDir)
    {
        String anchorFileName = artifactName + MuleDeploymentService.ARTIFACT_ANCHOR_SUFFIX;
        return new File(artifactDir, anchorFileName);
    }

    protected void addExplodedAppFromResource(String resource) throws IOException, URISyntaxException
    {
        addExplodedAppFromResource(resource, null);
    }

    protected void addExplodedAppFromResource(String resource, String appName) throws IOException, URISyntaxException
    {
        addExplodedArtifactFromResource(resource, appName, MULE_CONFIG_XML_FILE, MuleFoldersUtil.getAppsFolder());
    }

    private void addExplodedArtifactFromResource(String resource, String artifactName, String configFileName, File destinationDir) throws IOException, URISyntaxException
    {
        final URL url = bundleContext.getBundle().getEntry(resource);
        //URL url = getClass().getResource(resource);
        assertNotNull("Test resource not found: " + url, url);

        String artifactFolder = artifactName;
        if (artifactFolder == null)
        {
            File file = new File(url.getFile());
            int index = file.getName().lastIndexOf(".");

            if (index > 0)
            {
                artifactFolder = file.getName().substring(0, index);
            }
            else
            {
                artifactFolder = file.getName();
            }
        }

        addExplodedArtifact(url, artifactFolder, configFileName, destinationDir);
    }

    /**
     * Copies a given app archive with a given target name to the apps folder for deployment
     */
    private void addExplodedArtifact(URL url, String artifactName, String configFileName, File destinationDir) throws IOException, URISyntaxException
    {
        ReentrantLock lock = deploymentService.getLock();

        lock.lock();
        try
        {
            // Resources in an OSGi don't have file URLs
            final File tempFile = File.createTempFile(artifactName, ".zip");
            FileUtils.copyStreamToFile(url.openStream(), tempFile);
            File tempFolder = new File(MuleFoldersUtil.getMuleHomeFolder(), artifactName);
            FileUtils.unzip(tempFile, tempFolder);

            // Under some platforms, file.lastModified is managed at second level, not milliseconds.
            // Need to update the config file lastModified ere to ensure that is different from previous value
            File configFile = new File(tempFolder, configFileName);
            if (configFile.exists())
            {
                configFile.setLastModified(System.currentTimeMillis() + FILE_TIMESTAMP_PRECISION_MILLIS);
            }

            File appFolder = new File(destinationDir, artifactName);

            if (appFolder.exists())
            {
                FileUtils.deleteTree(appFolder);
            }

            FileUtils.moveDirectory(tempFolder, appFolder);
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * Removes a given application anchor file in order to start application undeployment
     *
     * @param appName name of application to undeployArtifact
     * @return true if anchor file was deleted, false otherwise
     */
    protected boolean removeAppAnchorFile(String appName)
    {
        File anchorFile = getArtifactAnchorFile(appName, MuleFoldersUtil.getAppsFolder());
        return anchorFile.delete();
    }

    protected void assertUndeploymentSuccess(final DeploymentListener listener, final String appName)
    {
        Prober prober = new PollingProber(DEPLOYMENT_TIMEOUT, 100);
        prober.check(new JUnitProbe()
        {
            @Override
            public boolean test()
            {
                verify(listener, times(1)).onUndeploymentSuccess(appName);
                return true;
            }

            @Override
            public String describeFailure()
            {
                return "Failed to undeployArtifact application: " + appName + System.lineSeparator() + super.describeFailure();
            }
        });
    }

    protected void assertDeploymentFailure(final DeploymentListener listener, final String artifactName)
    {
        assertDeploymentFailure(listener, artifactName, times(1));
    }

    protected void assertDeploymentFailure(final DeploymentListener listener, final String artifactName, final VerificationMode mode)
    {
        Prober prober = new PollingProber(DEPLOYMENT_TIMEOUT, 100);
        prober.check(new Probe()
        {
            @Override
            public boolean isSatisfied()
            {
                try
                {
                    verify(listener, mode).onDeploymentFailure(eq(artifactName), any(Throwable.class));
                    return true;
                }
                catch (AssertionError e)
                {
                    return false;
                }
            }

            @Override
            public String describeFailure()
            {
                return "Application deployment was supposed to fail for: " + artifactName;
            }
        });
    }

    protected void assertAppFolderIsMaintained(String appName)
    {
        assetArtifactFolderIsMaintained(appName, MuleFoldersUtil.getAppsFolder());
    }

    private void assetArtifactFolderIsMaintained(String artifactName, File artifactDir)
    {
        Prober prober = new PollingProber(DEPLOYMENT_TIMEOUT, 100);
        File appFolder = new File(artifactDir, artifactName);
        prober.check(new FileExists(appFolder));
    }

    protected Map<URL, Long> getZombieApplications()
    {
        final Map<URL, Long> zombieApplications;
        if (deploymentService instanceof MuleDeploymentService)
        {
            MuleDeploymentService muleDeploymentService = (MuleDeploymentService) deploymentService;
            zombieApplications = muleDeploymentService.getZombieApplications();
        }
        else
        {
            zombieApplications = new HashMap<>();
        }

        return zombieApplications;
    }

    /**
     * Allows to execute custom actions before or after executing logic or checking preconditions / verifications.
     */
    protected interface Action
    {
        void perform() throws Exception;
    }

    protected void deploysAppAndVerifyAnchorFileIsCreatedAfterDeploymentEnds(Action deployArtifactAction) throws Exception
    {
        Action verifyAnchorFileDoesNotExistsAction = new Action()
        {
            @Override
            public void perform() throws Exception
            {
                assertApplicationAnchorFileDoesNotExists(waitAppDescriptor.id);
            }
        };
        Action verifyDeploymentSuccessfulAction = new Action()
        {
            @Override
            public void perform() throws Exception
            {
                assertApplicationDeploymentSuccess(applicationDeploymentListener, waitAppDescriptor.id);
            }
        };
        Action verifyAnchorFileExistsAction = new Action()
        {
            @Override
            public void perform() throws Exception
            {
                assertApplicationAnchorFileExists(waitAppDescriptor.id);
            }
        };
        deploysArtifactAndVerifyAnchorFileCreatedWhenDeploymentEnds(deployArtifactAction, verifyAnchorFileDoesNotExistsAction, verifyDeploymentSuccessfulAction, verifyAnchorFileExistsAction);
    }

    protected void assertApplicationAnchorFileDoesNotExists(String applicationName)
    {
        assertThat(getArtifactAnchorFile(applicationName, MuleFoldersUtil.getAppsFolder()).exists(), is(false));
    }

    private void deploysArtifactAndVerifyAnchorFileCreatedWhenDeploymentEnds(Action deployArtifactAction,
                                                                             Action verifyAnchorFileDoesNotExistsAction,
                                                                             Action verifyDeploymentSuccessfulAction,
                                                                             Action verifyAnchorFileExistsAction
    ) throws Exception
    {
        WaitComponent.reset();
        deploymentService.start();
        deployArtifactAction.perform();
        try
        {
            if (!WaitComponent.componentInitializedLatch.await(DEPLOYMENT_TIMEOUT, TimeUnit.MILLISECONDS))
            {
                fail("WaitComponent should be already initialized. Probably app deployment failed");
            }
            verifyAnchorFileDoesNotExistsAction.perform();
        }
        finally
        {
            WaitComponent.waitLatch.release();
        }
        verifyDeploymentSuccessfulAction.perform();
        verifyAnchorFileExistsAction.perform();
    }

    protected void assertNoDeploymentInvoked(final DeploymentListener deploymentListener)
    {
        //TODO(pablo.kraan): look for a better way to test this
        boolean invoked;
        Prober prober = new PollingProber(DeploymentDirectoryWatcher.DEFAULT_CHANGES_CHECK_INTERVAL_MS * 2, 100);
        try
        {
            prober.check(new Probe()
            {
                @Override
                public boolean isSatisfied()
                {
                    try
                    {
                        verify(deploymentListener, times(1)).onDeploymentStart(any(String.class));
                        return true;
                    }
                    catch (AssertionError e)
                    {
                        return false;
                    }
                }

                @Override
                public String describeFailure()
                {
                    return "No deployment has started";
                }
            });

            invoked = true;
        }
        catch (AssertionError e)
        {
            invoked = false;
        }

        assertFalse("A deployment was started", invoked);
    }
}