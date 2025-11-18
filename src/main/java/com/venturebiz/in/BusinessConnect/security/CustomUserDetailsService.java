package com.venturebiz.in.BusinessConnect.security;

import com.venturebiz.in.BusinessConnect.model.User;
import com.venturebiz.in.BusinessConnect.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailAddress) throws UsernameNotFoundException {

        User user = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + emailAddress));

        return new org.springframework.security.core.userdetails.User(
                user.getEmailAddress(),
                "",  // No password required (OTP login)
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
