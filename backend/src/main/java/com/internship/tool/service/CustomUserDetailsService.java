package com.internship.tool.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Replace with real user lookup logic
        if ("user".equals(username)) {
            return User.withUsername("user")
                    .password("$2a$10$7QJ8QwQwQwQwQwQwQwQwQOQwQwQwQwQwQwQwQwQwQwQwQwQwQwQw") // bcrypt for 'password'
                    .authorities(new ArrayList<>())
                    .build();
        }
        throw new UsernameNotFoundException("User not found");
    }
}
