package MindChatBot.mindChatBot.service;

import MindChatBot.mindChatBot.model.ChatLog;
import MindChatBot.mindChatBot.repository.ChatLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class OpenAiService {

    private final WebClient webClient;
    private final ChatLogRepository chatLogRepository;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.model:gpt-4.1-nano}")
    private String model;

    @Value("${openai.system.prompt:You are a helpful and empathetic mental health assistant.}")
    private String systemPrompt;

    public OpenAiService(WebClient.Builder webClientBuilder, ChatLogRepository chatLogRepository) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.chatLogRepository = chatLogRepository;
    }

    public Mono<String> sendMessageToOpenAI(String message, String userId) {
        return Mono.fromCallable(() -> chatLogRepository.findByUserIdOrderByTimestampAsc(userId))
                .map(historyList -> {
                    // Use only the latest 5 chat history messages
                    int size = historyList.size();
                    if (size > 5) {
                        return historyList.subList(size - 5, size);
                    }
                    return historyList;
                })
                .flatMap(history -> {
                    List<Map<String, String>> messages = new ArrayList<>();

                    // System prompt: define chatbot role and tone
                    messages.add(Map.of("role", "system", "content", systemPrompt));

                    // Add previous chat history
                    for (ChatLog log : history) {
                        messages.add(Map.of("role", "user", "content", log.getMessage()));
                        messages.add(Map.of("role", "assistant", "content", log.getResponse()));
                    }

                    // Add the latest user message
                    messages.add(Map.of("role", "user", "content", message));

                    Map<String, Object> requestBody = Map.of(
                            "model", model,
                            "messages", messages
                    );

                    // Call OpenAI Chat Completion API
                    return webClient.post()
                            .uri("/chat/completions")
                            .header("Authorization", "Bearer " + openaiApiKey)
                            .header("Content-Type", "application/json")
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .map(this::extractMessage)
                            .map(opt -> opt.orElse("No response received from OpenAI."))
                            .onErrorReturn("Failed to communicate with OpenAI.");
                });
    }

    private Optional<String> extractMessage(Map<?, ?> response) {
        try {
            List<?> choices = (List<?>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) choice.get("message");
                return Optional.ofNullable((String) message.get("content"));
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    public Flux<ChatLog> getChatHistory(String userId) {
        List<ChatLog> logs = chatLogRepository.findByUserIdOrderByTimestampAsc(userId);
        return Flux.fromIterable(logs);
    }

    public Mono<Void> saveChatLog(String userId, String message, String response) {
        ChatLog log = new ChatLog(userId, message, response);
        return Mono.fromCallable(() -> chatLogRepository.save(log))
                .then();
    }
}
