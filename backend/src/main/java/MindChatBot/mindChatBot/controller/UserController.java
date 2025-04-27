package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.dto.AddUserRequest;
import MindChatBot.mindChatBot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@Controller // ❗ 여기 수정
public class UserController {

    private final UserService userService;

    // 회원가입 처리
    @PostMapping("/user")
    public String signUp(AddUserRequest request) {
        userService.save(request);
        return "redirect:/login";
    }
    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // templates/admin.html
    }
    // 로그인 후 사용자 홈 화면
    @GetMapping("/user")
    public String userHomePage(Principal principal, Model model) {
        String email = principal.getName(); // Getting the logged-in user's email
        System.out.println("Logged in user's email: " + email); // Debugging line
        model.addAttribute("email", email); // Passing the email to the view
        return "index"; // Returning the HTML page
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler()
                .logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
}
