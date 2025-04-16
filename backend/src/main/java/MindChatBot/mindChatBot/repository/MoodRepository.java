package MindChatBot.mindChatBot.repository;

import MindChatBot.mindChatBot.model.Mood;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MoodRepository extends MongoRepository<Mood, String> {
    List<Mood> findByYearAndMonth(int year, int month);
}
