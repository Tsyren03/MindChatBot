package com.mindChatBot.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chatbot_logs")
public class ChatBotLog {
    @Id
    private String id;
    private String userId;
    private String userQuery;
    private String botResponse;
    private double sentimentScore;
    private LocalDateTime timestamp;
}
