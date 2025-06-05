package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.ChatLog;
import MindChatBot.mindChatBot.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
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

        return openAiService.getChatHistory(userId)
                .collectList()
                .flatMap(historyList -> {
                    // Optionally include only last few messages
                    int maxHistory = Math.min(5, historyList.size());
                    List<ChatLog> recentHistory = historyList.subList(historyList.size() - maxHistory, historyList.size());

                    return openAiService.sendMessageToOpenAI(recentHistory, message, userId)
                            .flatMap(response -> openAiService.saveChatLog(userId, message, response)
                                    .thenReturn(ResponseEntity.ok(Map.of("response", response))));
                })
                .onErrorResume(error -> {
                    error.printStackTrace();
                    return Mono.just(ResponseEntity.status(500).body(Map.of("error", "An error occurred while communicating with OpenAI.")));
                });
    }

    @GetMapping("/history/{userId}")
    public Mono<ResponseEntity<List<ChatLog>>> getChatHistory(@PathVariable String userId) {
        return openAiService.getChatHistory(userId)
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()));
    }
}
