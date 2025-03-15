package com.mindChatBot.backend.service;

import com.mindChatBot.backend.model.EmotionLog;
import com.mindChatBot.backend.repository.EmotionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmotionLogService {

    private final EmotionLogRepository emotionLogRepository;

    @Autowired
    public EmotionLogService(EmotionLogRepository emotionLogRepository) {
        this.emotionLogRepository = emotionLogRepository;
    }

    // Save a new emotion log
    public EmotionLog saveEmotionLog(EmotionLog emotionLog) {
        return emotionLogRepository.save(emotionLog);
    }

    // Get all emotion logs for a user
    public List<EmotionLog> getEmotionLogsByUserId(String userId) {
        return emotionLogRepository.findByUserId(userId);
    }

    // Get emotion logs within a date range
    public List<EmotionLog> getEmotionLogsByDateRange(String userId, String startDate, String endDate) {
        return emotionLogRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }

    // Get a single emotion log by ID
    public Optional<EmotionLog> getEmotionLogById(String id) {
        return emotionLogRepository.findById(id);
    }

    // Delete an emotion log by ID
    public void deleteEmotionLog(String id) {
        emotionLogRepository.deleteById(id);
    }
}
