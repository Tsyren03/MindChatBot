package com.mindChatBot.backend.repository;

import com.mindChatBot.backend.model.EmotionLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmotionLogRepository extends MongoRepository<EmotionLog, String> {
    // Get all emotion logs for a specific user
    List<EmotionLog> findByUserId(String userId);

    // Get emotion logs within a date range for a user
    List<EmotionLog> findByUserIdAndDateBetween(String userId, String startDate, String endDate);
}