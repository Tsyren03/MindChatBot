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

    @Value("${openai.system.prompt}")
    private String systemPrompt;

    public OpenAiService(WebClient.Builder webClientBuilder, ChatLogRepository chatLogRepository) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.chatLogRepository = chatLogRepository;
    }

    public Mono<String> sendMessageToOpenAI(List<ChatLog> history, String message, String userId) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 사용자 이름 추출 (이메일에서 @ 앞부분, 또는 userId가 이름이면 이름으로)
        String userName = null;
        if (userId != null && !userId.equals("anonymous")) {
            if (userId.contains("@")) {
                userName = userId.substring(0, userId.indexOf("@"));
            } else {
                userName = userId;
            }
        }

        // application.yml에서 systemPrompt를 읽어와 사용
        String personalizedPrompt = systemPrompt;
        messages.add(Map.of("role", "system", "content", personalizedPrompt));

        // 기존 히스토리 추가
        for (ChatLog chat : history) {
            messages.add(Map.of("role", "user", "content", chat.getMessage()));
            messages.add(Map.of("role", "assistant", "content", chat.getResponse()));
        }

        // 현재 사용자 메시지 추가
        messages.add(Map.of("role", "user", "content", message));

        // 전체 요청을 Map으로 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        // 사용자 이름을 알면 user_name으로 전달 (OpenAI API는 system prompt에서만 사용)
        if (userName != null) {
            requestBody.put("user", userName);
        }

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + openaiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(responseMap -> {
                    try {
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                        if (choices != null && !choices.isEmpty()) {
                            Map<String, Object> messageMap = (Map<String, Object>) choices.get(0).get("message");
                            String content = (String) messageMap.get("content");
                            return Mono.justOrEmpty(content);
                        }
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error parsing OpenAI response", e));
                    }
                    return Mono.empty();
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
