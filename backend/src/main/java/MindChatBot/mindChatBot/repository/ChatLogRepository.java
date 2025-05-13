package MindChatBot.mindChatBot.repository;

import MindChatBot.mindChatBot.model.ChatLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatLogRepository extends MongoRepository<ChatLog, String> {
    // Custom query method to find all chat logs by userId
    Flux<ChatLog> findByUserId(String userId);
}
