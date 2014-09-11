/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.util;

import static org.ops4j.pax.exam.CoreOptions.options;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class SampleTest {

    //@Inject
    //private HelloService helloService;

    @Configuration
    public Option[] config() {

        return options(
                //mavenBundle("com.example.myproject", "myproject-api", "1.0.0-SNAPSHOT"),
                //bundle("http://www.example.com/repository/foo-1.2.3.jar"),
                //junitBundles()
        );
    }

    @Test
    public void getHelloService() {
        //assertNotNull(helloService);
        //assertEquals("Hello Pax!", helloService.getMessage());
    }
}