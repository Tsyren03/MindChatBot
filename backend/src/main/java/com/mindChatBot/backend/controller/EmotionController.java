package com.mindChatBot.backend.controller;
import com.example.mentalhealth.model.EmotionLog;
import com.example.mentalhealth.service.EmotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emotions")
public class EmotionController {

    @Autowired
    private EmotionService emotionService;

    @PostMapping
    public EmotionLog saveEmotion(@RequestBody EmotionLog emotionLog) {
        return emotionService.saveEmotion(emotionLog);
    }

    @GetMapping("/{userId}")
    public List<EmotionLog> getEmotions(@PathVariable String userId) {
        return emotionService.getEmotionsByUser(userId);
    }
}