package com.mindChatBot.backend.controller;

import com.mindChatBot.backend.model.EmotionLog;
import com.mindChatBot.backend.service.EmotionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emotions")
public class EmotionController {

    private final EmotionLogService emotionLogService;

    @Autowired
    public EmotionController(EmotionLogService emotionLogService) {
        this.emotionLogService = emotionLogService;
    }

    @PostMapping
    public EmotionLog saveEmotion(@RequestBody EmotionLog emotionLog) {
        return emotionLogService.saveEmotionLog(emotionLog);
    }

    @GetMapping("/{userId}")
    public List<EmotionLog> getEmotions(@PathVariable String userId) {
        return emotionLogService.getEmotionLogsByUserId(userId);
    }
}
