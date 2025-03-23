package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.EmotionLog;
import MindChatBot.mindChatBot.service.EmotionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emotion-logs")
public class EmotionLogController {

    @Autowired
    private EmotionLogService emotionLogService;

    @GetMapping
    public ResponseEntity<List<EmotionLog>> getAllLogs() {
        List<EmotionLog> logs = emotionLogService.findAllLogs();
        return ResponseEntity.ok(logs);
    }

    @PostMapping
    public ResponseEntity<EmotionLog> createLog(@RequestBody EmotionLog log) {
        EmotionLog savedLog = emotionLogService.saveLog(log);
        return ResponseEntity.ok(savedLog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable String id) {
        emotionLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}