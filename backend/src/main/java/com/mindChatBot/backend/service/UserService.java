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

    // Save or update a user
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Get a list of all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // You can add more methods for other user-related functionality if needed
}
