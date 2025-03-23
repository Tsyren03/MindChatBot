package com.mindChatBot.backend.controller;

import com.mindChatBot.backend.model.User;
import com.mindChatBot.backend.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    private final UserRepository userRepository;

    public UserProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public Optional<User> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername());
    }
}
