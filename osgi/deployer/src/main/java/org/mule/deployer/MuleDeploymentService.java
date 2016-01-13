/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.deployer;

import static org.mule.deployer.ArtifactDeploymentTemplate.NOP_ARTIFACT_DEPLOYMENT_TEMPLATE;
import static org.mule.deployer.DefaultArchiveDeployer.ZIP_FILE_SUFFIX;
import org.mule.deployer.api.ApplicationBundle;
import org.mule.deployer.api.DeploymentListener;
import org.mule.deployer.api.DeploymentService;
import org.mule.deployer.application.ApplicationBundleFactory;
import org.mule.deployer.util.DebuggableReentrantLock;
import org.mule.deployer.util.ObservableList;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.equinox.region.RegionDigraph;
import org.osgi.framework.BundleContext;

public class MuleDeploymentService implements DeploymentService
{

    public static final String ARTIFACT_ANCHOR_SUFFIX = "-anchor.txt";
    public static final IOFileFilter ZIP_ARTIFACT_FILTER = new AndFileFilter(new SuffixFileFilter(ZIP_FILE_SUFFIX), FileFileFilter.FILE);

    protected transient final Log logger = LogFactory.getLog(getClass());

    // fair lock
    private final ReentrantLock deploymentLock = new DebuggableReentrantLock(true);

    private final ObservableList<ApplicationBundle> applications = new ObservableList<>();
    //private final ObservableList<Domain> domains = new ObservableList<Domain>();
    //private final List<StartupListener> startupListeners = new ArrayList<StartupListener>();

    //private final CompositeDeploymentListener domainDeploymentListener = new CompositeDeploymentListener();
    //private final ArchiveDeployer<Domain> domainDeployer;
    private final DeploymentDirectoryWatcher deploymentDirectoryWatcher;
    private DefaultArchiveDeployer applicationDeployer;

    public MuleDeploymentService(BundleContext bundleContext, DeploymentListener applicationDeploymentListener, RegionDigraph regions)
    {
        //DomainClassLoaderRepository domainClassLoaderRepository = new MuleDomainClassLoaderRepository();
        //
        //ApplicationClassLoaderFactory applicationClassLoaderFactory = new MuleApplicationClassLoaderFactory(domainClassLoaderRepository, new DefaultNativeLibraryFinderFactory());
        //applicationClassLoaderFactory = new CompositeApplicationClassLoaderFactory(applicationClassLoaderFactory, pluginClassLoaderManager);
        //DefaultDomainFactory domainFactory = new DefaultDomainFactory(domainClassLoaderRepository);
        //domainFactory.setDeploymentListener(domainDeploymentListener);
        //DefaultApplicationFactory applicationFactory = new DefaultApplicationFactory(applicationClassLoaderFactory, domainFactory);
        //applicationFactory.setDeploymentListener(applicationDeploymentListener);
        //
        //ArtifactDeployer<Application> applicationMuleDeployer = new DefaultArtifactDeployer<Application>();
        //ArtifactDeployer<Domain> domainMuleDeployer = new DefaultArtifactDeployer<Domain>();
        //
        //this.applicationDeployer = new DefaultArchiveDeployer<>(applicationMuleDeployer, applicationFactory, applications, deploymentLock, NOP_ARTIFACT_DEPLOYMENT_TEMPLATE);
        //this.applicationDeployer.setDeploymentListener(applicationDeploymentListener);
        //this.domainDeployer = new DomainArchiveDeployer(
        //        new DefaultArchiveDeployer<>(domainMuleDeployer, domainFactory, domains, deploymentLock,
        //                new DomainDeploymentTemplate(applicationDeployer, this)),
        //        applicationDeployer, applicationMuleDeployer, this);
        //this.domainDeployer.setDeploymentListener(domainDeploymentListener);
        //this.deploymentDirectoryWatcher = new DeploymentDirectoryWatcher(domainDeployer, applicationDeployer, domains, applications, deploymentLock);
        ArtifactBundleDeployer<ApplicationBundle> applicationMuleDeployer = new DefaultArtifactBundleDeployer<>();


        this.applicationDeployer = new DefaultArchiveDeployer(applicationMuleDeployer, applications, deploymentLock, NOP_ARTIFACT_DEPLOYMENT_TEMPLATE, new ApplicationBundleFactory(bundleContext, regions));
        this.applicationDeployer.setDeploymentListener(applicationDeploymentListener);

        deploymentDirectoryWatcher = new DeploymentDirectoryWatcher(applicationDeployer, applications, deploymentLock);
    }

    @Override
    public void start()
    {
        //DeploymentStatusTracker deploymentStatusTracker = new DeploymentStatusTracker();
        //addDeploymentListener(deploymentStatusTracker.getApplicationDeploymentStatusTracker());
        //addDomainDeploymentListener(deploymentStatusTracker.getDomainDeploymentStatusTracker());
        //
        //StartupSummaryDeploymentListener summaryDeploymentListener = new StartupSummaryDeploymentListener(deploymentStatusTracker, this);
        //addStartupListener(summaryDeploymentListener);

        deploymentDirectoryWatcher.start();

        //for (StartupListener listener : startupListeners)
        //{
        //    try
        //    {
        //        listener.onAfterStartup();
        //    }
        //    catch (Throwable t)
        //    {
        //        logger.error(t);
        //    }
        //}
    }

    @Override
    public void stop()
    {
        deploymentDirectoryWatcher.stop();
    }

    //@Override
    //public Domain findDomain(String domainName)
    //{
    //    return deploymentDirectoryWatcher.findArtifact(domainName, domains);
    //}

    @Override
    public ApplicationBundle findApplication(String appName)
    {
        return deploymentDirectoryWatcher.findArtifact(appName, applications);
    }

    //@Override
    //public Collection<Application> findDomainApplications(final String domain)
    //{
    //    Preconditions.checkArgument(domain != null, "Domain name cannot be null");
    //    return CollectionUtils.select(applications, new Predicate()
    //    {
    //        @Override
    //        public boolean evaluate(Object object)
    //        {
    //            return ((Application) object).getDomain().getArtifactName().equals(domain);
    //        }
    //    });
    //}


    @Override
    public List<ApplicationBundle> getApplications()
    {
        return Collections.unmodifiableList(applications);
    }

    //@Override
    //public List<Domain> getDomains()
    //{
    //    return Collections.unmodifiableList(domains);
    //}

    /**
     * @return URL/lastModified of apps which previously failed to deploy
     */
    public Map<URL, Long> getZombieApplications()
    {
        return applicationDeployer.getArtifactsZombieMap();
    }

    //Map<URL, Long> getZombieDomains()
    //{
    //    return domainDeployer.getArtifactsZombieMap();
    //}

    @Override
    public ReentrantLock getLock()
    {
        return deploymentLock;
    }

    @Override
    public void undeploy(String appName)
    {
        applicationDeployer.undeployArtifact(appName);
    }

    @Override
    public void deploy(URL appArchiveUrl) throws IOException
    {
        applicationDeployer.deployPackagedArtifact(appArchiveUrl);
    }

    @Override
    public void redeploy(String artifactName)
    {
        //TODO(pablo.kraan): OSGi - add redeploy
        //try
        //{
        //    applicationDeployer.redeploy(findApplication(artifactName));
        //}
        //catch (DeploymentException e)
        //{
        //    if (logger.isDebugEnabled())
        //    {
        //        logger.debug("Failure while redeploying application: " + artifactName, e);
        //    }
        //}
    }

    //@Override
    //public void undeployDomain(String domainName)
    //{
    //    domainDeployer.undeployArtifact(domainName);
    //}

    //@Override
    //public void deployDomain(URL domainArchiveUrl) throws IOException
    //{
    //    domainDeployer.deployPackagedArtifact(domainArchiveUrl);
    //}

    //@Override
    //public void redeployDomain(String domainName)
    //{
    //    domainDeployer.redeploy(findDomain(domainName));
    //}

    //@Override
    //public void addStartupListener(StartupListener listener)
    //{
    //    this.startupListeners.add(listener);
    //}

    //@Override
    //public void removeStartupListener(StartupListener listener)
    //{
    //    this.startupListeners.remove(listener);
    //}

    //@Override
    //public void addDeploymentListener(DeploymentListener listener)
    //{
    //    applicationDeploymentListener.addDeploymentListener(listener);
    //}

    //@Override
    //public void removeDeploymentListener(DeploymentListener listener)
    //{
    //    applicationDeploymentListener.removeDeploymentListener(listener);
    //}

    //@Override
    //public void addDomainDeploymentListener(DeploymentListener listener)
    //{
    //    domainDeploymentListener.addDeploymentListener(listener);
    //}
    //
    //@Override
    //public void removeDomainDeploymentListener(DeploymentListener listener)
    //{
    //    domainDeploymentListener.removeDeploymentListener(listener);
    //}
    //
    //public void setDomainFactory(DomainFactory domainFactory)
    //{
    //    this.domainDeployer.setArtifactFactory(domainFactory);
    //}

    //void undeploy(Domain domain)
    //{
    //    domainDeployer.undeployArtifact(domain.getArtifactName());
    //}
}
