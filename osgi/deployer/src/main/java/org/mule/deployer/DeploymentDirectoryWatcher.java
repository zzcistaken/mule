/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.deployer;

import static org.mule.util.SplashScreen.miniSplash;
import org.mule.config.StartupContext;
import org.mule.deployer.api.DeploymentException;
import org.mule.deployer.api.ApplicationBundle;
import org.mule.deployer.api.ArtifactBundle;
import org.mule.deployer.util.DebuggableReentrantLock;
import org.mule.deployer.util.ElementAddedEvent;
import org.mule.deployer.util.ElementRemovedEvent;
import org.mule.deployer.util.ObservableList;
import org.mule.util.ArrayUtils;
import org.mule.util.CollectionUtils;
import org.mule.util.StringUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * It's in charge of the whole deployment process.
 * <p/>
 * It will deploy the applications at the container startup process.
 * It will periodically scan the artifact directories in order to process new deployments,
 * remove artifacts that were previously deployed but the anchor file was removed and redeploy
 * those applications which configuration has changed.
 */
public class DeploymentDirectoryWatcher implements Runnable
{

    //TODO(pablo.kraan): OSGi - avoid duplciating these constants
    public static final String ZIP_FILE_SUFFIX = ".zip";
    public static final String ARTIFACT_NAME_PROPERTY = "artifactName";
    public static final String ARTIFACT_ANCHOR_SUFFIX = "-anchor.txt";

    public static final String CHANGE_CHECK_INTERVAL_PROPERTY = "mule.launcher.changeCheckInterval";
    public static final IOFileFilter ZIP_ARTIFACT_FILTER = new AndFileFilter(new SuffixFileFilter(ZIP_FILE_SUFFIX), FileFileFilter.FILE);
    public static final int DEFAULT_CHANGES_CHECK_INTERVAL_MS = 5000;

    protected transient final Log logger = LogFactory.getLog(getClass());

    private final ReentrantLock deploymentLock;
    //TODO(pablo.kraan): OSGi - add domains
    //private final ArchiveDeployer<Domain> domainArchiveDeployer;
    //private final ArtifactTimestampListener<Domain> domainTimestampListener;
    //private final ObservableList<Domain> domains;

    private final ArchiveDeployer applicationArchiveDeployer;
    private final ArtifactTimestampListener applicationTimestampListener;
    private final File appsDir;
    private final ObservableList<ApplicationBundle> applications;
    //private final File domainsDir;
    private ScheduledExecutorService artifactDirMonitorTimer;

    protected volatile boolean dirty;

    public DeploymentDirectoryWatcher(final ArchiveDeployer applicationArchiveDeployer, ObservableList<ApplicationBundle> applications, final ReentrantLock deploymentLock)
    {
        this.deploymentLock = deploymentLock;
        //this.domainsDir = domainArchiveDeployer.getDeploymentDirectory();
        //this.domainArchiveDeployer = domainArchiveDeployer;
        //this.domains = domains;
        this.applicationArchiveDeployer = applicationArchiveDeployer;
        this.appsDir = applicationArchiveDeployer.getDeploymentDirectory();
        this.applications = applications;
        applications.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent e)
            {
                if (e instanceof ElementAddedEvent || e instanceof ElementRemovedEvent)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Deployed applications set has been modified, flushing state.");
                    }
                    dirty = true;
                }
            }
        });
        //domains.addPropertyChangeListener(new PropertyChangeListener()
        //{
        //    public void propertyChange(PropertyChangeEvent e)
        //    {
        //        if (e instanceof ElementAddedEvent || e instanceof ElementRemovedEvent)
        //        {
        //            if (logger.isDebugEnabled())
        //            {
        //                logger.debug("Deployed applications set has been modified, flushing state.");
        //            }
        //            dirty = true;
        //        }
        //    }
        //});
        this.applicationTimestampListener = new ArtifactTimestampListener(applications);
        //this.domainTimestampListener = new ArtifactTimestampListener(domains);
    }

    /**
     * Starts the process of deployment / undeployment of artifact.
     * <p/>
     * It wil schedule a task for periodically scan the deployment directories.
     */
    public void start()
    {
        try
        {
            deploymentLock.lock();
            deleteAllAnchors();

            // mule -app app1:app2:app3 will restrict deployment only to those specified apps
            final Map<String, Object> options = StartupContext.get().getStartupOptions();
            String appString = (String) options.get("app");

            //String[] explodedDomains = domainsDir.list(DirectoryFileFilter.DIRECTORY);
            //String[] packagedDomains = domainsDir.list(ZIP_ARTIFACT_FILTER);
            //
            //deployPackedDomains(packagedDomains);
            //deployExplodedDomains(explodedDomains);

            if (appString == null)
            {
                String[] explodedApps = appsDir.list(DirectoryFileFilter.DIRECTORY);
                String[] packagedApps = appsDir.list(ZIP_ARTIFACT_FILTER);

                deployPackedApps(packagedApps);
                deployExplodedApps(explodedApps);
            }
            else
            {
                String[] apps = appString.split(":");
                apps = removeDuplicateAppNames(apps);

                for (String app : apps)
                {
                    try
                    {
                        File applicationFile = new File(appsDir, app + ZIP_FILE_SUFFIX);

                        if (applicationFile.exists() && applicationFile.isFile())
                        {
                            applicationArchiveDeployer.deployPackagedArtifact(app + ZIP_FILE_SUFFIX);
                        }
                        else
                        {
                            applicationArchiveDeployer.deployExplodedArtifact(app);
                        }
                    }
                    catch (Exception e)
                    {
                        // Ignore and continue
                    }
                }
            }
        }
        finally
        {
            if (deploymentLock.isHeldByCurrentThread())
            {
                deploymentLock.unlock();
            }
        }

        //TODO(pablo.kraan): OSGi - re-add check for appString property
        // only start the monitor thread if we launched in default mode without explicitly
        // stated applications to launch
        //if (!(appString != null))
        //{
            scheduleChangeMonitor();
        //}
        //else
        //{
        //    if (logger.isInfoEnabled())
        //    {
        //        logger.info(miniSplash("Mule is up and running in a fixed app set mode"));
        //    }
        //}
    }

    /**
     * Stops the deployment scan service.
     */
    public void stop()
    {
        stopAppDirMonitorTimer();

        deploymentLock.lock();
        try
        {
            stopArtifacts(applications);
            //stopArtifacts(domains);
        }
        finally
        {
            deploymentLock.unlock();
        }
    }

    private void stopArtifacts(List<? extends ArtifactBundle> artifacts)
    {
        Collections.reverse(artifacts);

        for (ArtifactBundle artifact : artifacts)
        {
            try
            {
                //TODO(pablo.kraan): OSGi - add stop and dispose if required
                //artifact.stop();
                //artifact.dispose();
            }
            catch (Throwable t)
            {
                logger.error(t);
            }
        }
    }

    private static int getChangesCheckIntervalMs()
    {
        try
        {
            String value = System.getProperty(CHANGE_CHECK_INTERVAL_PROPERTY);
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            return DEFAULT_CHANGES_CHECK_INTERVAL_MS;
        }
    }

    private void scheduleChangeMonitor()
    {
        final int reloadIntervalMs = getChangesCheckIntervalMs();
        artifactDirMonitorTimer = Executors.newSingleThreadScheduledExecutor(new ArtifactDeployerMonitorThreadFactory());

        artifactDirMonitorTimer.scheduleWithFixedDelay(this,
                                                       0,
                                                       reloadIntervalMs,
                                                       TimeUnit.MILLISECONDS);

        if (logger.isInfoEnabled())
        {
            logger.info(miniSplash(String.format("Mule is up and kicking (every %dms)", reloadIntervalMs)));
        }
    }

    private void deployPackedApps(String[] zips)
    {
        for (String zip : zips)
        {
            try
            {
                applicationArchiveDeployer.deployPackagedArtifact(zip);
            }
            catch (Exception e)
            {
                // Ignore and continue
            }
        }
    }

    private void deployExplodedApps(String[] apps)
    {
        for (String addedApp : apps)
        {
            try
            {
                applicationArchiveDeployer.deployExplodedArtifact(addedApp);
            }
            catch (DeploymentException e)
            {
                // Ignore and continue
            }
        }
    }

    // Cycle is:
    //   undeployArtifact removed apps
    //   undeployArtifact removed domains
    //   deploy domain archives
    //   deploy domain exploded
    //   redeploy modified apps
    //   deploy archives apps
    //   deploy exploded apps
    public void run()
    {
        try
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Checking for changes...");
            }
            // use non-barging lock to preserve fairness, according to javadocs
            // if there's a lock present - wait for next poll to do anything
            if (!deploymentLock.tryLock(0, TimeUnit.SECONDS))
            {
                if (logger.isDebugEnabled())
                {
                    //TODO(pablo.kraan): OSGi - remove this downcast
                    logger.debug("Another deployment operation in progress, will skip this cycle. Owner thread: " +
                                 ((DebuggableReentrantLock) deploymentLock).getOwner());
                }
                return;
            }

            undeployRemovedApps();

            undeployRemovedDomains();

            //// list new apps
            //String[] domains = domainsDir.list(DirectoryFileFilter.DIRECTORY);
            //
            //final String[] domainZips = domainsDir.list(ZIP_ARTIFACT_FILTER);
            //
            //redeployModifiedDomains();
            //
            //deployPackedDomains(domainZips);
            //
            //// re-scan exploded domains and update our state, as deploying Mule domains archives might have added some
            //if (domainZips.length > 0 || dirty)
            //{
            //    domains = domainsDir.list(DirectoryFileFilter.DIRECTORY);
            //}
            //
            //deployExplodedDomains(domains);

            redeployModifiedApplications();

            // list new apps
            String[] apps = appsDir.list(DirectoryFileFilter.DIRECTORY);

            final String[] appZips = appsDir.list(ZIP_ARTIFACT_FILTER);

            deployPackedApps(appZips);

            // re-scan exploded apps and update our state, as deploying Mule app archives might have added some
            if (appZips.length > 0 || dirty)
            {
                apps = appsDir.list(DirectoryFileFilter.DIRECTORY);
            }

            deployExplodedApps(apps);
        }
        catch (Exception e)
        {
            // preserve the flag for the thread
            Thread.currentThread().interrupt();
        }
        finally
        {
            if (deploymentLock.isHeldByCurrentThread())
            {
                deploymentLock.unlock();
            }
            dirty = false;
        }
    }

    public <T extends ArtifactBundle> T findArtifact(String artifactName, ObservableList<T> artifacts)
    {
        return (T) CollectionUtils.find(artifacts, new BeanPropertyValueEqualsPredicate(ARTIFACT_NAME_PROPERTY, artifactName));
    }

    private void undeployRemovedDomains()
    {
        //undeployRemovedArtifacts(domainsDir, domains, domainArchiveDeployer);
    }

    private void undeployRemovedApps()
    {
        undeployRemovedArtifacts(appsDir, applications);
    }

    private void undeployRemovedArtifacts(File artifactDir, ObservableList<? extends ApplicationBundle> artifacts)
    {
        // we care only about removed anchors
        String[] currentAnchors = artifactDir.list(new SuffixFileFilter(ARTIFACT_ANCHOR_SUFFIX));
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Current anchors:%n"));
            for (String currentAnchor : currentAnchors)
            {
                sb.append(String.format("  %s%n", currentAnchor));
            }
            logger.debug(sb.toString());
        }

        String[] artifactAnchors = findExpectedAnchorFiles(artifacts);
        @SuppressWarnings("unchecked")
        final Collection<String> deletedAnchors = CollectionUtils.subtract(Arrays.asList(artifactAnchors), Arrays.asList(currentAnchors));
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Deleted anchors:%n"));
            for (String deletedAnchor : deletedAnchors)
            {
                sb.append(String.format("  %s%n", deletedAnchor));
            }
            logger.debug(sb.toString());
        }

        for (String deletedAnchor : deletedAnchors)
        {
            String artifactName = StringUtils.removeEnd(deletedAnchor, ARTIFACT_ANCHOR_SUFFIX);
            try
            {
                if (findArtifact(artifactName, artifacts) != null)
                {
                    applicationArchiveDeployer.undeployArtifact(artifactName);
                }
                else if (logger.isDebugEnabled())
                {
                    logger.debug(String.format("Artifact [%s] has already been undeployed via API", artifactName));
                }
            }
            catch (Throwable t)
            {
                logger.error("Failed to undeployArtifact artifact: " + artifactName, t);
            }
        }
    }

    /**
     * Returns the list of anchor file names for the deployed apps
     *
     * @return a non null list of file names
     */
    private String[] findExpectedAnchorFiles(ObservableList<? extends ArtifactBundle> artifacts)
    {
        String[] anchors = new String[artifacts.size()];
        int i = 0;
        for (ArtifactBundle artifact : artifacts)
        {
            anchors[i++] = artifact.getArtifactName() + ARTIFACT_ANCHOR_SUFFIX;
        }
        return anchors;
    }

    //private void deployExplodedDomains(String[] domains)
    //{
    //    for (String addedApp : domains)
    //    {
    //        try
    //        {
    //            domainArchiveDeployer.deployExplodedArtifact(addedApp);
    //        }
    //        catch (DeploymentException e)
    //        {
    //            // Ignore and continue
    //        }
    //    }
    //}
    //
    //private void deployPackedDomains(String[] zips)
    //{
    //    for (String zip : zips)
    //    {
    //        try
    //        {
    //            domainArchiveDeployer.deployPackagedArtifact(zip);
    //        }
    //        catch (Exception e)
    //        {
    //            // Ignore and continue
    //        }
    //    }
    //}

    private void deleteAllAnchors()
    {
        //deleteAnchorsFromDirectory(domainsDir);
        deleteAnchorsFromDirectory(appsDir);
    }

    private void deleteAnchorsFromDirectory(final File directory)
    {
        // Deletes any leftover anchor files from previous shutdowns
        String[] anchors = directory.list(new SuffixFileFilter(ARTIFACT_ANCHOR_SUFFIX));
        for (String anchor : anchors)
        {
            // ignore result
            new File(directory, anchor).delete();
        }
    }

    private String[] removeDuplicateAppNames(String[] apps)
    {
        List<String> appNames = new LinkedList<String>();

        for (String appName : apps)
        {
            if (!appNames.contains(appName))
            {
                appNames.add(appName);
            }
        }

        return appNames.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    //private void redeployModifiedDomains()
    //{
    //    redeployModifiedArtifacts(domains, domainTimestampListener, domainArchiveDeployer);
    //}

    private void redeployModifiedApplications()
    {
        Collection redeployableApplications = CollectionUtils.select(applications, new Predicate()
        {
            @Override
            public boolean evaluate(Object object)
            {
                //TODO(pablo.kraan): OSGi - add check for app deployment
                //return ((ApplicationBundle) object).getDescriptor().isRedeploymentEnabled();
                return false;
            }
        });
        redeployModifiedArtifacts(redeployableApplications, applicationTimestampListener, applicationArchiveDeployer);
    }

    private void redeployModifiedArtifacts(Collection<ApplicationBundle> artifacts, ArtifactTimestampListener artifactTimestampListener, ArchiveDeployer artifactArchiveDeployer)
    {
        //TODO(pablo.kraan): OSGi - add redeploy
        //for (T artifact : artifacts)
        //{
        //    if (artifactTimestampListener.isArtifactResourceUpdated(artifact))
        //    {
        //        try
        //        {
        //            artifactArchiveDeployer.redeploy(artifact);
        //        }
        //        catch (DeploymentException e)
        //        {
        //            if (logger.isDebugEnabled())
        //            {
        //                logger.debug(e);
        //            }
        //        }
        //    }
        //}
    }

    private void stopAppDirMonitorTimer()
    {
        if (artifactDirMonitorTimer != null)
        {
            artifactDirMonitorTimer.shutdown();
            try
            {
                artifactDirMonitorTimer.awaitTermination(getChangesCheckIntervalMs(), TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ArtifactTimestampListener<T extends ArtifactBundle> implements PropertyChangeListener
    {

        private Map<String, ArtifactResourcesTimestamp> artifactConfigResourcesTimestaps = new HashMap<>();

        public ArtifactTimestampListener(ObservableList<T> artifacts)
        {
            artifacts.addPropertyChangeListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent event)
        {
            if (event instanceof ElementAddedEvent)
            {
                T artifactAdded = (T) event.getNewValue();
                artifactConfigResourcesTimestaps.put(artifactAdded.getArtifactName(), new ArtifactResourcesTimestamp(artifactAdded));
            }
            else if (event instanceof ElementRemovedEvent)
            {
                T artifactRemoved = (T) event.getNewValue();
                artifactConfigResourcesTimestaps.remove(artifactRemoved.getArtifactName());
            }
        }

        public boolean isArtifactResourceUpdated(T artifact)
        {
            ArtifactResourcesTimestamp applicationResourcesTimestamp = artifactConfigResourcesTimestaps.get(artifact.getArtifactName());
            return !applicationResourcesTimestamp.resourcesHaveSameTimestamp(artifact);
        }
    }

    private static class ArtifactResourcesTimestamp<T extends ArtifactBundle>
    {

        private final Map<String, Long> timestampsPerResource = new HashMap<String, Long>();

        public ArtifactResourcesTimestamp(final T artifact)
        {
            //TODO(pablo.kraan): OSGi - add timestamps per resource
            //for (File configResourceFile : artifact.getResourceFiles())
            //{
            //    timestampsPerResource.put(configResourceFile.getAbsolutePath(), configResourceFile.lastModified());
            //}
        }

        public boolean resourcesHaveSameTimestamp(final T artifact)
        {
            boolean resourcesHaveSameTimestamp = true;
            //TODO(pablo.kraan): OSGi - implement resource change detection
            //for (File configResourceFile : artifact.getResourceFiles())
            //{
            //    long originalTimestamp = timestampsPerResource.get(configResourceFile.getAbsolutePath());
            //    long currentTimestamp = configResourceFile.lastModified();
            //
            //    if (originalTimestamp != currentTimestamp)
            //    {
            //        timestampsPerResource.put(configResourceFile.getAbsolutePath(), currentTimestamp);
            //        resourcesHaveSameTimestamp = false;
            //    }
            //}
            return resourcesHaveSameTimestamp;
        }
    }
}




