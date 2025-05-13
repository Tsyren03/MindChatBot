package MindChatBot.mindChatBot.service;

import MindChatBot.mindChatBot.model.Mood;
import MindChatBot.mindChatBot.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<String, Double> getMoodStatistics(String userId) {
        // Get all moods for the given user
        List<Mood> moods = moodRepository.findByUserId(userId);

        // Initialize a map to store the counts of each mood
        Map<String, Integer> moodCounts = new HashMap<>();
        moodCounts.put("bad", (int) moods.stream().filter(m -> m.getEmoji().equals("bad")).count());
        moodCounts.put("poor", (int) moods.stream().filter(m -> m.getEmoji().equals("poor")).count());
        moodCounts.put("neutral", (int) moods.stream().filter(m -> m.getEmoji().equals("neutral")).count());
        moodCounts.put("good", (int) moods.stream().filter(m -> m.getEmoji().equals("good")).count());
        moodCounts.put("best", (int) moods.stream().filter(m -> m.getEmoji().equals("best")).count());

        // Calculate the total count of all moods
        int totalMoods = moodCounts.values().stream().mapToInt(Integer::intValue).sum();

        // If no moods are found, return 0% for all moods
        if (totalMoods == 0) {
            return moodCounts.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> 0.0));
        }

        // Convert counts to percentages and store them in a new map
        Map<String, Double> moodPercentages = new HashMap<>();
        for (Map.Entry<String, Integer> entry : moodCounts.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalMoods;
            moodPercentages.put(entry.getKey(), percentage);
        }

        return moodPercentages;
    }
    public List<Mood> getAllMoodsForUser(String userId) {
        return moodRepository.findByUserId(userId);
    }
}
