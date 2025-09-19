package com.security.SpringSecurityAuth.controller;

import com.security.SpringSecurityAuth.configuration.JwtUtils;
import com.security.SpringSecurityAuth.entity.User;
import com.security.SpringSecurityAuth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    // Permet de créer un nouvel utilisateur
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        // Vérifie si un user existe déjà dans la base
        if(userRepository.findByUsername(user.getUsername()) != null){
            return ResponseEntity.badRequest().body("This Username is already use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.ok(userRepository.save(user));
    }

    // Permet à un utilisateur existant de pouvoir se connecter
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        try{
            // Vérification des identifiants (username + mot de passe)
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            if (authentication.isAuthenticated()){

                // On récupère l'utilisateur en base pour avoir ses rôles
                User dbUser = userRepository.findByUsername(user.getUsername());

                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtils.generateToken(dbUser.getUsername(), dbUser.getRoles()));
                authData.put("type", "Bearer");
                return ResponseEntity.ok(authData);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username or Password");
        } catch (AuthenticationException e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username or Password");
        }
    }
}
