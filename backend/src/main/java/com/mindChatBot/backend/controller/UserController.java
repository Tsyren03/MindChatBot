package com.mindChatBot.backend.controller;

import com.mindChatBot.backend.model.User;
import com.mindChatBot.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")  // Keeping "/api/user" for user-related operations
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // You can add other user-related endpoints here (e.g., update user, delete user, etc.)
}
