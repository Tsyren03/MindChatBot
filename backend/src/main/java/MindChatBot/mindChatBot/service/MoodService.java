package MindChatBot.mindChatBot.service;

import MindChatBot.mindChatBot.model.Mood;
import MindChatBot.mindChatBot.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class MoodService {

    @Autowired
    private MoodRepository moodRepository;

    public List<Mood> getMoodsByMonth(String userId, int year, int month) {
        return moodRepository.findByUserIdAndYearAndMonth(userId, year, month);
    }

    public Mood saveMood(String userId, Mood mood) {
        Mood existingMood = moodRepository.findByUserIdAndYearAndMonthAndDay(userId, mood.getYear(), mood.getMonth(), mood.getDay());

        if (existingMood != null) {
            existingMood.setEmoji(mood.getEmoji());
            return moodRepository.save(existingMood);
        } else {
            mood.setUserId(userId); // Ensure userId is set
            return moodRepository.save(mood);  // Save new mood
        }
    }

    public Map<String, Integer> getMoodStatistics(String userId) {
        // userId에 해당하는 사용자의 mood 통계 처리
        List<Mood> moods = moodRepository.findByUserId(userId);

        // 통계 카운트 (예: bad, poor, neutral, good, best)
        Map<String, Integer> stats = new HashMap<>();
        stats.put("bad", (int) moods.stream().filter(m -> m.getEmoji().equals("bad")).count());
        stats.put("poor", (int) moods.stream().filter(m -> m.getEmoji().equals("poor")).count());
        stats.put("neutral", (int) moods.stream().filter(m -> m.getEmoji().equals("neutral")).count());
        stats.put("good", (int) moods.stream().filter(m -> m.getEmoji().equals("good")).count());
        stats.put("best", (int) moods.stream().filter(m -> m.getEmoji().equals("best")).count());

        return stats;
    }
}
