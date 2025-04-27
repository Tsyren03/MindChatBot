package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OpenAiService openAiService;

    @Autowired
    public ChatController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, String>>> chatWithBot(@RequestBody Map<String, String> requestBody) {
        String message = requestBody.get("message");
        String userId = requestBody.getOrDefault("userId", "anonymous");

        if (message == null || message.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Message is empty.")));
        }

        return openAiService.sendMessageToOpenAI(message, userId)
                .map(response -> ResponseEntity.ok(Map.of("response", response)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(500).body(Map.of("error", "Error communicating with OpenAI.")))
                );
    }
}
