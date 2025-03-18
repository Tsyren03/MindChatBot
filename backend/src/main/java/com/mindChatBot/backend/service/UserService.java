package com.mindChatBot.backend.service;

import com.mindChatBot.backend.model.User;
import com.mindChatBot.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Optional<User>> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    // New register method
    public User register(User user) {
        // Logic for user registration (e.g., save user to the database)
        return userRepository.save(user);
    }

    // Existing login method
    public String login(User user) {
        // Logic for user login
        return "Login successful for user: " + user.getUsername();
    }
}
