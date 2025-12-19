package com.yahyabank.security;

import com.yahyabank.auth_users.entity.User;
import com.yahyabank.auth_users.repo.UserRepo;
import com.yahyabank.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("Inside extracting username from token loadUserByUsername(email)");

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Exception occurred Email Not Found"));

//        log.error("Exception occurred while extracting username from token");


        return AuthUser.builder()
                .user(user)
                .build();


    }
}
