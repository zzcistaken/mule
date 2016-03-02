/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.routing;

import org.mule.tck.junit4.FunctionalTestCase;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

public class ScatterGatherRouterTestCase extends FunctionalTestCase
{
    @Rule
    public ContiPerfRule rule = new ContiPerfRule();

    @Override
    public int getTestTimeoutSecs()
    {
        return 120;
    }

    @Override
    protected String getConfigFile()
    {
        return "scatter-gather-perf-test.xml";
    }

    @Test

    @Required(throughput = 130, average = 8, percentile90 = 9)
    @PerfTest(duration = 15000, threads = 1, warmUp = 5000)
    public void parallelProcessing() throws Exception
    {
        this.testFlow("parallelProcessing", getTestEvent(""));
    }

    @Test
    @Required(throughput = 50, average = 18, percentile90 = 20)
    @PerfTest(duration = 15000, threads = 1, warmUp = 5000)
    public void sequentialProcessing() throws Exception
    {
        this.testFlow("sequentialProcessing", getTestEvent(""));
    }

    @Test
    @Required(throughput = 260, average = 4, percentile90 = 5)
    @PerfTest(duration = 15000, threads = 1, warmUp = 5000)
    public void parallelHttpProcessing() throws Exception
    {
        this.testFlow("parallelHttpProcessing", getTestEvent(""));
    }

    @Test
    @Required(throughput = 120, average = 8, percentile90 = 10)
    @PerfTest(duration = 15000, threads = 1, warmUp = 5000)
    public void sequentialHttpProcessing() throws Exception
    {
        this.testFlow("sequentialHttpProcessing", getTestEvent(""));
    }

    @Test
    @Required(throughput = 420, average = 23, percentile90 = 40)
    @PerfTest(duration = 15000, threads = 10, warmUp = 5000)
    public void parallelHttMultiThreadedpProcessing() throws Exception
    {
        this.testFlow("parallelHttpProcessing", getTestEvent(""));
    }

    @Test
    @Required(throughput = 700, average = 15, percentile90 = 20)
    @PerfTest(duration = 15000, threads = 10, warmUp = 5000)
    public void sequentialHttpMultiThreadedProcessing() throws Exception
    {
        this.testFlow("sequentialHttpProcessing", getTestEvent(""));
    }
}
