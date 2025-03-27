package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.EmotionLog;
import MindChatBot.mindChatBot.service.EmotionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/emotion-logs")
public class EmotionLogController {

    @Autowired
    private EmotionLogService emotionLogService;

    // Get all logs
    @GetMapping
    public ResponseEntity<List<EmotionLog>> getAllLogs() {
        List<EmotionLog> logs = emotionLogService.findAllLogs();
        return ResponseEntity.ok(logs);
    }

    // Get logs by date
    @GetMapping("/date")
    public ResponseEntity<List<EmotionLog>> getLogsByDate(@RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date); // Parse the date string into LocalDate
            List<EmotionLog> logs = emotionLogService.findLogsByDate(localDate);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            // Return a bad request response if date parsing fails
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Create a new log
    @PostMapping
    public ResponseEntity<EmotionLog> createLog(@RequestBody EmotionLog log) {
        EmotionLog savedLog = emotionLogService.saveLog(log);
        return ResponseEntity.ok(savedLog);
    }

    // Delete a log by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable String id) {
        emotionLogService.deleteLog(id);
        return ResponseEntity.ok().build(); // Return a 200 OK status after deletion
    }
}
