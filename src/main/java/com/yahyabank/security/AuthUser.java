package com.yahyabank.security;

import com.yahyabank.auth_users.entity.User;
import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class AuthUser implements UserDetails {


    //===============================================================================================================================================

    private User user;

    //===============================================================================================================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

   //===============================================================================================================================================

//    @Nullable
    @Override
    public  String getPassword() {
        return user.getPassword();
    }

   //===============================================================================================================================================

    @Override
    public String getUsername() {
        return user.getEmail();
    }

   //===============================================================================================================================================

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    //===============================================================================================================================================

}
