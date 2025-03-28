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

    public List<Mood> getMoodsByMonth(int year, int month) {
        return moodRepository.findByYearAndMonth(year, month);
    }

    public Mood saveMood(Mood mood) {
        return moodRepository.save(mood);
    }

    public Map<String, Integer> getMoodStatistics() {
        List<Mood> allMoods = moodRepository.findAll();
        Map<String, Integer> stats = new HashMap<>();

        stats.put("bad", 0);
        stats.put("poor", 0);
        stats.put("neutral", 0);
        stats.put("good", 0);
        stats.put("best", 0);

        for (Mood mood : allMoods) {
            stats.put(mood.getEmoji(), stats.getOrDefault(mood.getEmoji(), 0) + 1);
        }

        return stats;
    }
}
