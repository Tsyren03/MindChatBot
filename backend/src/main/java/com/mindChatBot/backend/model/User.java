package com.mindChatBot.backend.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private List<EmotionLog> emotionLogs;
    private List<JournalEntry> journalEntries;
}