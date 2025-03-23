package MindChatBot.mindChatBot.repository;

import MindChatBot.mindChatBot.model.ChatBotLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatBotLogRepository extends MongoRepository<ChatBotLog, String> {
}
