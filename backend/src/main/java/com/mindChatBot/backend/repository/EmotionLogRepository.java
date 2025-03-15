package com.mindChatBot.backend.repository;

import com.mindChatBot.backend.model.EmotionLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmotionLogRepository extends MongoRepository<EmotionLog, String> {
    List<EmotionLog> findByUserId(String userId);
    List<EmotionLog> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);
}
