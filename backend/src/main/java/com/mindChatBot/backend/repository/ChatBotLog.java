package com.mindChatBot.backend.repository;

import com.mindChatBot.backend.model.ChatBotLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatBotLogRepository extends MongoRepository<ChatBotLog, String> {
    // Get chatbot logs for a specific user
    List<ChatBotLog> findByUserId(String userId);

    // Get chatbot logs containing specific keywords in user queries
    List<ChatBotLog> findByUserQueryContainingIgnoreCase(String keyword);
}
