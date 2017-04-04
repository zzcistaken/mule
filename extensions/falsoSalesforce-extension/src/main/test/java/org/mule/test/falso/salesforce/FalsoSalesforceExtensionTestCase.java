/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package java.org.mule.test.falso.salesforce;

import org.mule.functional.junit4.ExtensionFunctionalTestCase;
import org.mule.test.falso.salesforce.FalsoSalesforceExtension;

public class FalsoSalesforceExtensionTestCase extends ExtensionFunctionalTestCase {

  @Override
  protected Class<?>[] getAnnotatedExtensionClasses() {
    return new Class[]{FalsoSalesforceExtension.class};
  }

}
