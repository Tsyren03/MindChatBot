package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.Mood;
import MindChatBot.mindChatBot.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moods")
public class MoodController {

    @Autowired
    private MoodService moodService;

    @GetMapping("/{year}/{month}")
    public List<Mood> getMoods(@PathVariable int year, @PathVariable int month) {
        return moodService.getMoodsByMonth(year, month);
    }

    @PostMapping
    public Mood saveMood(@RequestBody Mood mood) {
        return moodService.saveMood(mood);
    }
    @GetMapping("/stats")
    public Map<String, Integer> getMoodStats() {
        return moodService.getMoodStatistics();
    }

}