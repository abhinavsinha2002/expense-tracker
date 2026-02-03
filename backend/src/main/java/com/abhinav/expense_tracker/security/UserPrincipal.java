package com.abhinav.expense_tracker.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.abhinav.expense_tracker.entity.User;

public class UserPrincipal implements OAuth2User,UserDetails {
    private Long id;
    private String email;
    private String password;
    private Collection<?extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(Long id,String email,String password,Collection<? extends GrantedAuthority> authorities ){
        this.id = id;
        this.email =email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user){
        return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public static UserPrincipal create(User user, Map<String,Object> attributes){
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    @Override
    public Map<String,Object> getAttributes(){
        return attributes;
    }

    public void setAttributes(Map<String,Object> attributes){
        this.attributes = attributes;
    }

    @Override
    public String getName(){
        return String.valueOf(id);
    }

    // --- UserDetails Methods ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return email; } // IMPORTANT: We use email as the unique username

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
    
    // Helper to get ID easily
    public Long getId() { return id; }
}
