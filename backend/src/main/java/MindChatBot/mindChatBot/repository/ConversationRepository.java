package MindChatBot.mindChatBot.repository;

import MindChatBot.mindChatBot.model.Conversation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends ReactiveMongoRepository<Conversation, String> {
    // You can add custom queries if needed
}