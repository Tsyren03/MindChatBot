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
@Controller
@RequestMapping("/user") // 공통 URL prefix
public class UserController {

    private final UserService userService;

    // 회원가입 처리
    @PostMapping("/register")
    public String signUp(@ModelAttribute AddUserRequest request) {
        userService.save(request);
        return "redirect:/login";
    }

    // 로그인 후 사용자 홈 화면
    @GetMapping("/home")
    public String userHomePage(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName(); // 로그인된 사용자의 이메일
            model.addAttribute("email", email);
        }
        return "index"; // templates/index.html
    }

    // 관리자 전용 페이지
    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // templates/admin.html
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler()
                .logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
}
