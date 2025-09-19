package com.security.SpringSecurityAuth.configuration;

import com.security.SpringSecurityAuth.filter.JwtFilter;
import com.security.SpringSecurityAuth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    // Définit l'encodeur de mot de passe (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Gère la logique de l'authentification
    // userDetailsService pour charger les utilisateurs
    // passwordEncoder pour vérifier les mots de passe
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception{
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    // Définit les règles de sécurité http
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/*").permitAll() // Toutes les requêtes pour cet API sont autorisés sans être connecté (login, signup)
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN") // accès uniquement pour les rôles admins
                        .anyRequest().authenticated()) // Les autres requêtes nécessitent d'être authentifié
                .addFilterBefore(new JwtFilter(customUserDetailsService, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
