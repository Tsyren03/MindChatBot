package MindChatBot.mindChatBot.controller;

import lombok.RequiredArgsConstructor;
import MindChatBot.mindChatBot.model.User;
import MindChatBot.mindChatBot.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class UserViewController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login"; // login.html
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup"; // signup.html
    }
}
