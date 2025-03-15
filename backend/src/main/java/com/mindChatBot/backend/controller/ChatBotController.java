package com.mindChatBot.backend.controller;

import com.mindChatBot.backend.model.ChatBotLog;
import com.mindChatBot.backend.service.ChatBotLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatBotController {

    private final ChatBotLogService chatBotLogService;

    @Autowired
    public ChatBotController(ChatBotLogService chatBotLogService) {
        this.chatBotLogService = chatBotLogService;
    }

    @PostMapping
    public ChatBotLog chat(@RequestBody ChatBotLog chatRequest) {
        return chatBotLogService.saveChatBotLog(chatRequest);
    }
}
