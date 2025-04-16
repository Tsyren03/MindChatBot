package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.service.RasaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private RasaService rasaService;


    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> payload) {
        String userId = payload.getOrDefault("userId", "anonymous");
        String message = payload.get("message");

        Map<String, String> response = new HashMap<>();

        if (message == null || message.trim().isEmpty()) {
            response.put("error", "Message is required.");
            return ResponseEntity.badRequest().body(response);
        }

        String rasaResponse = rasaService.sendMessageToRasa(message, userId);


        response.put("response", rasaResponse);
        return ResponseEntity.ok().body(response);
    }

}
