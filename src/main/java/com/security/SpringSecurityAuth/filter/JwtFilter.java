package com.security.SpringSecurityAuth.filter;

import com.security.SpringSecurityAuth.configuration.JwtUtils;
import com.security.SpringSecurityAuth.service.CustomUserDetailsService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter { // Filtre quis'exécute une seule fois par requête

    private final CustomUserDetailsService customerUserDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Récupère l'en-tête "Authorization" de la requête HTTP
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Vérifie que l'en-tête existe et commence bien par "Bearer"
        if(authHeader != null && authHeader.startsWith("Bearer ")){

            // Extrait uniquement le token (après "Bearer ")
            jwt = authHeader.substring(7);

            // Récupère l'username contenu dans le token
            username = jwtUtils.extractUsername(jwt);
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

            // Charge les détails de l'utilisateur (roles, password hash, etc) depuis la DB
            UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);

            // Vérifie si le token est bien valide pour cet utilisateur
            if(jwtUtils.validateToken(jwt, userDetails)){
                // Crée un objet d'authentification reconnu par Spring Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Enregistre l'objet d'authentification dans le contexte de sécurité
            }
        }

        filterChain.doFilter(request, response);
    }

}
