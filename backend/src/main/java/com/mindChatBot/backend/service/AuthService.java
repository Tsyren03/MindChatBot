package com.mindChatBot.backend.service;

import com.mindChatBot.backend.model.User;
import com.mindChatBot.backend.repository.UserRepository;
import com.mindChatBot.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            System.out.println("User found: " + user.get().getEmail());
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                System.out.println("Password matches.");
                return jwtUtil.generateToken(email);
            } else {
                System.out.println("Password does not match.");
            }
        } else {
            System.out.println("User not found.");
        }
        throw new BadCredentialsException("Invalid credentials");
    }

    public String register(String email, String password) {
        // Check if a user with the same email already exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            // If user exists, throw a custom exception
            throw new RuntimeException("User already exists with the email: " + email);
        }

        // If no user with the same email exists, create a new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));  // Ensure the password is hashed

        // Save the new user into the database
        userRepository.save(newUser);

        // Generate and return the JWT token for the new user
        return jwtUtil.generateToken(email);
    }
}