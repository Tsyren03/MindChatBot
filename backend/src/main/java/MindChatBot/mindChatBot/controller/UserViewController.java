package MindChatBot.mindChatBot.controller;

import lombok.RequiredArgsConstructor;
import MindChatBot.mindChatBot.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UserViewController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup"; // templates/signup.html
    }
}
