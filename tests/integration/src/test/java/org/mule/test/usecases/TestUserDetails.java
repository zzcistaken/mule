package org.mule.test.usecases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 */
public class TestUserDetails implements UserDetails
{

    private final String username;
    private final String password;
    private boolean isEnabled = true;
    private final List<GrantedAuthority> grantedAuthorities;

    public TestUserDetails(String username, String password, String... authorities)
    {
        this.username = username;
        this.password = password;
        grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (String authority : authorities)
        {
            grantedAuthorities.add(new GrantedAuthorityImpl(authority));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return Collections.unmodifiableList(grantedAuthorities);
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return isEnabled;
    }

    public TestUserDetails disable()
    {
        this.isEnabled = false;
        return this;
    }
}
