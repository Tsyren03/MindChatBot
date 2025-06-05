package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.User;
import MindChatBot.mindChatBot.repository.UserRepository;
import MindChatBot.mindChatBot.dto.AddUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/user/profile")
    @ResponseBody
    public Map<String, Object> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        if (userDetails == null) {
            result.put("error", "Not logged in");
            return result;
        }
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            result.put("error", "User not found");
            return result;
        }
        result.put("name", user.getName());
        result.put("email", user.getEmail());
        result.put("joined", user.getCreatedAt() != null ? user.getCreatedAt().toString().substring(0, 10) : "-");
        return result;
    }

    @PostMapping("/user/register")
    public String register(@ModelAttribute AddUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "redirect:/signup?error=exists";
        }
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getName())
                .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화
                .createdAt(java.time.LocalDateTime.now())
                .build();
        userRepository.save(user);
        return "redirect:/login";
    }

    @PostMapping("/user/profile/upload-image")
    @ResponseBody
    public Map<String, String> uploadProfileImage(@RequestParam("profilePic") MultipartFile file, Principal principal) throws IOException {
        Map<String, String> result = new HashMap<>();
        if (principal == null) {
            result.put("error", "Not logged in");
            return result;
        }
        if (file == null || file.isEmpty()) {
            result.put("error", "No file uploaded");
            return result;
        }
        String userEmail = principal.getName();
        String uploadDir = "src/main/resources/static/uploads/profile-images/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = userEmail + "_" + System.currentTimeMillis() + "_" + originalFilename;
        File dest = new File(dir, filename);
        file.transferTo(dest);
        // (선택) DB에 이미지 경로 저장: /uploads/profile-images/filename
        // ... User 엔티티에 profileImageUrl 필드가 있다면 저장 로직 추가 ...
        result.put("imageUrl", "/uploads/profile-images/" + filename);
        return result;
    }
}
