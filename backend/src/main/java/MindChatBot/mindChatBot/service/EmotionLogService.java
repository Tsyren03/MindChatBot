package MindChatBot.mindChatBot.service;

import MindChatBot.mindChatBot.model.EmotionLog;
import MindChatBot.mindChatBot.repository.EmotionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmotionLogService {

    @Autowired
    private EmotionLogRepository emotionLogRepository;

    public List<EmotionLog> findAllLogs() {
        return emotionLogRepository.findAll();
    }

    public EmotionLog saveLog(EmotionLog log) {
        return emotionLogRepository.save(log);
    }

    public void deleteLog(String id) {
        emotionLogRepository.deleteById(id);
    }
}