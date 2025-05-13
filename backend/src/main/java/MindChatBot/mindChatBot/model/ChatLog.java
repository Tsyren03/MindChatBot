package MindChatBot.mindChatBot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "chat_logs")
public class ChatLog {

    @Id
    private String id;
    private String userId;
    private String message;
    private String response;
    private LocalDateTime timestamp;
    public ChatLog(String userId, String message, String response) {
        this.userId = userId;
        this.message = message;
        this.response = response;
        this.timestamp = LocalDateTime.now();
    }
    // No need to manually create constructors or getters/setters as Lombok handles it.
}
