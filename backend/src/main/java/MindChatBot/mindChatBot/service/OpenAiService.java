package MindChatBot.mindChatBot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class OpenAiService {

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    public OpenAiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
    }

    public Mono<String> sendMessageToOpenAI(String message, String userId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4.1-nano");
        requestBody.put("messages", new Object[] {
                Map.of("role", "system", "content", "You are a helpful assistant."),
                Map.of("role", "user", "content", message)
        });

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + openaiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    try {
                        var choices = (java.util.List<Map<String, Object>>) response.get("choices");
                        if (choices != null && !choices.isEmpty()) {
                            var messageMap = (Map<String, Object>) choices.get(0).get("message");
                            return (String) messageMap.get("content");
                        }
                        return "No response from OpenAI.";
                    } catch (Exception e) {
                        return "Error parsing OpenAI response.";
                    }
                });
    }
}