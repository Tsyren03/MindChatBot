package MindChatBot.mindChatBot.repository;

import MindChatBot.mindChatBot.model.ChatLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLogRepository extends MongoRepository<ChatLog, String> {
    // You can add custom queries here if needed (e.g., find by userId)
}
