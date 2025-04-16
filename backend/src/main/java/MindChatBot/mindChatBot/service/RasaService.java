package MindChatBot.mindChatBot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RasaService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String sendMessageToRasa(String message, String userId) {
        String rasaUrl = "http://localhost:5005/webhooks/rest/webhook"; // Rasa 서버 주소

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", userId);
        payload.put("message", message);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(rasaUrl, request, String.class);
            String responseBody = response.getBody();

            // Rasa 응답에서 텍스트 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);

            StringBuilder finalResponse = new StringBuilder();
            for (JsonNode node : root) {
                if (node.has("text")) {
                    finalResponse.append(node.get("text").asText()).append(" ");
                }
            }

            return finalResponse.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error communicating with Rasa.";
        }
    }
}