package org.mule.test;

import org.mule.test.usecases.TestUserDetails;

import java.util.Arrays;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 *
 */
public class TestUserService extends InMemoryUserDetailsManager
{

    public TestUserService()
    {
        super(Arrays.<UserDetails>asList(new TestUserDetails("john","john","ROLE_USER"),
                                         new TestUserDetails("marie","marie","ROLE_ADMIN"),
                                         new TestUserDetails("peter","peter","ROLE_ADMIN").disable()));
    }

}
