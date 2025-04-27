package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OpenAiService openAiService;

    @Autowired
    public ChatController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping
    public ResponseEntity<String> sendMessageToChatBot(@RequestBody String userMessage) {
        try {
            // Send message to OpenAI service and get the response
            String botResponse = openAiService.chatWithBot(userMessage);
            return ResponseEntity.ok(botResponse);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("An error occurred while processing the request.");
        }
    }
}
