package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.Mood;
import MindChatBot.mindChatBot.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/user/moods")
public class MoodController {

    private final MoodService moodService;

    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }
    @PostMapping("/fetch")
    public List<Mood> getMoodsByJson(@RequestBody Map<String, Integer> request) {
        String userId = getCurrentUserId();
        Integer year = request.get("year");
        Integer month = request.get("month");

        if (year == null || month == null) {
            throw new IllegalArgumentException("Year and month must be provided.");
        }

        return moodService.getMoodsByMonth(userId, year, month);
    }

    @PostMapping("/save")
    public Mood saveMood(@RequestBody Mood mood) {
        String userId = getCurrentUserId();
        return moodService.saveMood(userId, mood);
    }
    @GetMapping("/stats")
    public Map<String, Integer> getMoodStats() {
        String userId = getCurrentUserId();  // **현재 로그인된 유저 ID를 가져온다**
        return moodService.getMoodStatistics(userId);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((MindChatBot.mindChatBot.model.User) authentication.getPrincipal()).getId();
    }
}
