/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.ssl.api;

/**
 * Provides methods to access the configuration for certificate revocation.
 *
 * "Standard" methods check the corresponding certificate fields (so they must be both enabled and present to be used).
 * "Custom" methods ignore certificate fields and force using a different source to check the certificate against.
 */
public interface TlsContextRevocationCheckConfiguration
{
    /**
     * @return The state of standard OCSP checks (enabled/disabled).
     */
    boolean getStandardOcsp();

    /**
     * @return The state of standard CRL distribution point checks (enabled/disabled).
     */
    boolean getStandardCrldp();

    /**
     * @return The CRL path (list of ignored certificates).
     */
    String getCustomCrlFile();

    /**
     * @return The custom OCSP URL (an address to ask if a certificate is valid).
     */
    String getCustomOcspUrl();
}
