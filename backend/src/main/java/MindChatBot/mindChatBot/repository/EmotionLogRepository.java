package MindChatBot.mindChatBot.repository;

import MindChatBot.mindChatBot.model.EmotionLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmotionLogRepository extends MongoRepository<EmotionLog, String> {
    List<EmotionLog> findByDate(LocalDate date);
}
