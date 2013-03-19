package org.mule.test;

/**
 *
 */
public class User
{

    private final String username;

    public User(String username)
    {
        this.username = username;
    }

    public boolean isActive()
    {
        return true;
    }

}
