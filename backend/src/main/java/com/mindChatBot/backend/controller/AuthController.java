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

        // Retrieve email and password from the request body
        String email = request.get("email");
        String password = request.get("password");

        // Call the authService to register the user
        try {
            String token = authService.register(email, password); // This will now handle uniqueness check
            return Map.of("token", token);
        } catch (RuntimeException e) {
            // Return an error message if the email is already taken
            return Map.of("error", e.getMessage());
        }
    }
}
