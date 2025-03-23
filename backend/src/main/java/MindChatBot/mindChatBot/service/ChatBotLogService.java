package MindChatBot.mindChatBot.service;

import MindChatBot.mindChatBot.model.ChatBotLog;
import MindChatBot.mindChatBot.repository.ChatBotLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatBotLogService {

    @Autowired
    private ChatBotLogRepository chatBotLogRepository;

    public List<ChatBotLog> findAllLogs() {
        return chatBotLogRepository.findAll();
    }

    public ChatBotLog saveLog(ChatBotLog log) {
        return chatBotLogRepository.save(log);
    }

    public void deleteLog(String id) {
        chatBotLogRepository.deleteById(id);
    }
}