package com.mindChatBot.backend.controller;

import com.mindChatBot.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        System.out.println("Login request received: " + request);
        String token = authService.login(request.get("email"), request.get("password"));
        return Map.of("token", token);
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> request) {
        System.out.println("Register request received: " + request);
        String token = authService.register(request.get("email"), request.get("password"));
        return Map.of("token", token);
    }

}
