package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.ChatLog;
import MindChatBot.mindChatBot.model.JournalEntry;
import MindChatBot.mindChatBot.service.JournalEntryService;
import MindChatBot.mindChatBot.service.MoodService;
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
    private final MoodService moodService;
    private final JournalEntryService journalEntryService;

    @Autowired
    public ChatController(OpenAiService openAiService,
                          MoodService moodService,
                          JournalEntryService journalEntryService) {
        this.openAiService = openAiService;
        this.moodService = moodService;
        this.journalEntryService = journalEntryService;
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, String>>> chatWithBot(@RequestBody Map<String, String> requestBody) {
        String message = requestBody.get("message");
        String userId = requestBody.getOrDefault("userId", "anonymous");

        if (message == null || message.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Message is empty.")));
        }

        return openAiService.sendMessageToOpenAI(message, userId)
                .flatMap(response -> openAiService.saveChatLog(userId, message, response)
                        .thenReturn(ResponseEntity.ok(Map.of("response", response))))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(500).body(Map.of("error", "An error occurred while communicating with OpenAI."))));
    }

    @GetMapping("/history/{userId}")
    public Mono<ResponseEntity<List<ChatLog>>> getChatHistory(@PathVariable String userId) {
        return openAiService.getChatHistory(userId)
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()));
    }

    @PostMapping("/sendAllData")
    public Mono<ResponseEntity<Map<String, String>>> sendAllDataToBot(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.getOrDefault("userId", "anonymous");
        StringBuilder messageBuilder = new StringBuilder();

        Map<String, Double> moodStats = moodService.getMoodStatistics(userId);

        if (moodStats.isEmpty() || moodStats.values().stream().allMatch(v -> v == 0.0)) {
            messageBuilder.append("No mood statistics recorded in the last 30 days. Would you like to share how you're feeling now?");
        } else {
            messageBuilder.append("User mood statistics for the last 30 days:\n")
                    .append("Bad: ").append(String.format("%.2f", moodStats.getOrDefault("bad", 0.0))).append("%\n")
                    .append("Good: ").append(String.format("%.2f", moodStats.getOrDefault("good", 0.0))).append("%\n")
                    .append("Neutral: ").append(String.format("%.2f", moodStats.getOrDefault("neutral", 0.0))).append("%\n");
        }

        List<JournalEntry> recentJournals = journalEntryService.getRecentEntries(userId, 1);

        if (recentJournals.isEmpty()) {
            messageBuilder.append("\nNo recent journal entries found. Would you like to write about how you feel?");
        } else {
            JournalEntry journal = recentJournals.get(0);
            String content = journal.getContent() != null ? journal.getContent().trim() : "";
            if (content.length() < 3 || content.matches(".*[\\W_].*")) {
                messageBuilder.append("\nThe journal entry is short or unclear. How about writing your thoughts in more detail?");
            } else {
                messageBuilder.append("\nRecent journal entry: ").append(content);
            }
        }

        String fullMessage = messageBuilder.toString();

        return openAiService.sendMessageToOpenAI(fullMessage, userId)
                .flatMap(response -> openAiService.saveChatLog(userId, fullMessage, response)
                        .thenReturn(ResponseEntity.ok(Map.of("response", response))))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(500).body(Map.of("error", "An error occurred while processing OpenAI response."))));
    }
}
