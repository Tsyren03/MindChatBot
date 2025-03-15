package com.mindChatBot.backend.controller;
import com.example.mentalhealth.model.ChatbotLog;
import com.example.mentalhealth.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping
    public ChatbotLog chat(@RequestBody ChatbotLog chatRequest) {
        return chatbotService.processChat(chatRequest);
    }
}