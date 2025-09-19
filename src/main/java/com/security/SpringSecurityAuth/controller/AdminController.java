package com.security.SpringSecurityAuth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("Bienvenue sur le dashboard admin !");
    }
}
