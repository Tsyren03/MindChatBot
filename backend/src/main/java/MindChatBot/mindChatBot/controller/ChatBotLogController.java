package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.ChatBotLog;
import MindChatBot.mindChatBot.service.ChatBotLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot-logs")
public class ChatBotLogController {

    @Autowired
    private ChatBotLogService chatBotLogService;

    @GetMapping
    public ResponseEntity<List<ChatBotLog>> getAllLogs() {
        List<ChatBotLog> logs = chatBotLogService.findAllLogs();
        return ResponseEntity.ok(logs);
    }

    @PostMapping
    public ResponseEntity<ChatBotLog> createLog(@RequestBody ChatBotLog log) {
        ChatBotLog savedLog = chatBotLogService.saveLog(log);
        return ResponseEntity.ok(savedLog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable String id) {
        chatBotLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}