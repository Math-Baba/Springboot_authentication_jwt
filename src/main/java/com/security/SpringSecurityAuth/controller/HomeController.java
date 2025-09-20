package com.security.SpringSecurityAuth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/api/home")
    public String welcome(@AuthenticationPrincipal OAuth2User principal) {
        String username = principal.getAttribute("name"); // récupère le nom depuis Google
        return "Welcome " + username + "!";
    }
}
