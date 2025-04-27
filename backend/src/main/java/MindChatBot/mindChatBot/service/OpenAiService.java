package MindChatBot.mindChatBot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenAiService {

    @Value("${openai.api.key}") // Make sure the property name is correct
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String chatWithBot(String userMessage) throws IOException {
        // Use the cheaper gpt-3.5-turbo model instead of gpt-4
        JsonNode requestBody = objectMapper.createObjectNode()
                .put("model", "gpt-3.5-turbo")  // Use gpt-3.5-turbo for cheaper usage
                .putArray("messages").add(objectMapper.createObjectNode()
                        .put("role", "system")
                        .put("content", "You are a helpful assistant."))
                .add(objectMapper.createObjectNode()
                        .put("role", "user")
                        .put("content", userMessage));

        // Prepare the request to OpenAI API
        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JsonNode responseBody = objectMapper.readTree(response.body().string());
                return responseBody.path("choices").get(0).path("message").path("content").asText();
            } else {
                throw new IOException("Error from OpenAI: " + response.body().string());
            }
        }
    }
}
