package MindChatBot.mindChatBot.repository;

import  MindChatBot.mindChatBot.model.EmotionLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmotionLogRepository extends MongoRepository<EmotionLog, String> {
}
