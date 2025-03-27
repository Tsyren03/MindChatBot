package MindChatBot.mindChatBot.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "emotion_logs")
public class EmotionLog {
    @Id
    private String id;
    private String userId;
    private LocalDate date;
    private int emotionScore;
    private String aiInsights;
}
