package com.security.SpringSecurityAuth.service;

import com.security.SpringSecurityAuth.entity.User;
import com.security.SpringSecurityAuth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Cherche l'utilisateur en base avec le username fourni
        User user = userRepository.findByUsername(username);

        // Si l'utilisateur n'existe pas, on lève une exception
        if(user == null){
            throw new UsernameNotFoundException("user not found with username: " + username);
        }

        // Convertit le Set<String> roles en authorities
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
