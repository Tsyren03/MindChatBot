package MindChatBot.mindChatBot.service;

import MindChatBot.mindChatBot.model.EmotionLog;
import MindChatBot.mindChatBot.repository.EmotionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmotionLogService {

    @Autowired
    private EmotionLogRepository emotionLogRepository;

    // Find all emotion logs
    public List<EmotionLog> findAllLogs() {
        return emotionLogRepository.findAll();
    }

    // Save an emotion log
    public EmotionLog saveLog(EmotionLog log) {
        return emotionLogRepository.save(log);
    }

    // Delete an emotion log by ID
    public void deleteLog(String id) {
        emotionLogRepository.deleteById(id);
    }

    // Find emotion logs by date
    public List<EmotionLog> findLogsByDate(LocalDate date) {
        return emotionLogRepository.findByDate(date);
    }
}
