package com.mindChatBot.backend.service;

import com.mindChatBot.backend.model.ChatBotLog;
import com.mindChatBot.backend.repository.ChatBotLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatBotLogService {

    private final ChatBotLogRepository chatBotLogRepository;

    @Autowired
    public ChatBotLogService(ChatBotLogRepository chatBotLogRepository) {
        this.chatBotLogRepository = chatBotLogRepository;
    }

    // Save chatbot interaction log
    public ChatBotLog saveChatBotLog(ChatBotLog chatBotLog) {
        return chatBotLogRepository.save(chatBotLog);
    }

    // Get chatbot logs for a user
    public List<ChatBotLog> getChatBotLogsByUserId(String userId) {
        return chatBotLogRepository.findByUserId(userId);
    }

    // Search chatbot logs by user query keyword
    public List<ChatBotLog> searchChatBotLogsByKeyword(String keyword) {
        return chatBotLogRepository.findByUserQueryContainingIgnoreCase(keyword);
    }

    // Delete chatbot log by ID
    public void deleteChatBotLog(String id) {
        chatBotLogRepository.deleteById(id);
    }
}
