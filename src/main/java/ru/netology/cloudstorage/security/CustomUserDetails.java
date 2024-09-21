package ru.netology.cloudstorage.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.netology.cloudstorage.model.User;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private User myUser;

    public CustomUserDetails(User user) {
        this.myUser = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return myUser.getRoles().stream().toList();
    }

    @Override
    public String getPassword() {
        return myUser.getPassword();
    }

    @Override
    public String getUsername() {
        return myUser.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
