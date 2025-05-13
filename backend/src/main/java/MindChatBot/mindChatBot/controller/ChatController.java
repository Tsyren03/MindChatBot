package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.JournalEntry;
import MindChatBot.mindChatBot.model.Mood;
import MindChatBot.mindChatBot.service.JournalEntryService;
import MindChatBot.mindChatBot.service.MoodService;
import MindChatBot.mindChatBot.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                .map(response -> ResponseEntity.ok(Map.of("response", response)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(500).body(Map.of("error", "Error communicating with OpenAI.")))
                );
    }

    @PostMapping("/sendAllData")
    public Mono<ResponseEntity<Map<String, String>>> sendAllDataToBot(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.getOrDefault("userId", "anonymous");
        // Prepare a compact message with a focus on mood data and analysis
        StringBuilder messageBuilder = new StringBuilder();

// Mood Statistics Check
        Map<String, Double> moodStatistics = moodService.getMoodStatistics(userId);
        if (moodStatistics.isEmpty() || moodStatistics.values().stream().allMatch(value -> value == 0.0)) {
            messageBuilder.append("It looks like your mood statistics haven't been recorded in the last 30 days. Would you like to share how you're feeling?");
        } else {
            messageBuilder.append("User Mood Stats (last 30 days):\n");
            messageBuilder.append("Bad: ").append(String.format("%.2f", moodStatistics.get("bad"))).append("%\n");
            messageBuilder.append("Good: ").append(String.format("%.2f", moodStatistics.get("good"))).append("%\n");
            messageBuilder.append("Neutral: ").append(String.format("%.2f", moodStatistics.get("neutral"))).append("%\n");
        }

// Journal Entry Check
        List<JournalEntry> latestEntries = journalEntryService.getRecentEntries(userId, 1);
        if (latestEntries.isEmpty()) {
            messageBuilder.append("\nIt looks like you don't have any recent journal entries. Would you like to write about how you're feeling?");
        } else {
            JournalEntry latest = latestEntries.get(0);
            if (latest.getContent().length() < 3 || latest.getContent().matches(".*[\\W_].*")) {
                messageBuilder.append("\nYour journal entry seems short or unclear. Feel free to share more about your thoughts or emotions.");
            } else {
                messageBuilder.append("\nRecent Journal: ").append(latest.getContent()).append("\n");
            }
        }

// Send the full message to OpenAI
        String fullMessage = messageBuilder.toString();

        return openAiService.sendMessageToOpenAI(fullMessage, userId)
                .map(response -> ResponseEntity.ok(Map.of("response", response)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(500).body(Map.of("error", "Failed to process data with OpenAI.")))
                );
    }
}