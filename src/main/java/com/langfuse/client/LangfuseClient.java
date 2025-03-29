package com.langfuse.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langfuse.config.LangfuseProperties;
import com.langfuse.prompt.model.ChatMessage;
import com.langfuse.prompt.model.Prompt;
import com.langfuse.prompt.model.Prompt.PromptBuilder;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class LangfuseClient {
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final LangfuseProperties properties;

    public LangfuseClient(LangfuseProperties properties) {
        this.properties = properties;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofMillis(properties.getHttpConnectionTimeOut()))
                .readTimeout(Duration.ofMillis(properties.getHttpReadTimeOut()))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 获取单条 prompt
     *
     * @param promptKey
     * @return
     */
    public Optional<Prompt> getPrompt(String promptKey) {
        String auth = Base64.getEncoder().encodeToString(
                (properties.getPublicKey() + ":" + properties.getSecretKey()).getBytes()
        );

        try {
            String encodedPromptKey = URLEncoder.encode(promptKey, StandardCharsets.UTF_8.toString());


        HttpUrl url = Objects.requireNonNull(
                        HttpUrl.parse(
                                properties.getBaseUrl() + "/api/public/v2/prompts/" + encodedPromptKey))
                .newBuilder()
                .addQueryParameter("label", properties.getPrompt().getEnvLabel())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Basic " + auth)
                .build();

        Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("Failed to fetch prompt {}: {}", promptKey, response.code());
                return Optional.empty();
            }

            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            return parsePromptFromJson(jsonNode);
        } catch (Exception e) {
            log.error("Error fetching prompt from Langfuse", e);
            return Optional.empty();
        }
    }

    /**
     * 获取全部prompt
     *
     * @return
     */
    public List<Prompt> getAllPrompts() {
        // Fetch all prompts from Langfuse
        String auth = Base64.getEncoder().encodeToString(
                (properties.getPublicKey() + ":" + properties.getSecretKey()).getBytes()
        );

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(properties.getBaseUrl() + "/api/public/v2/prompts"))
                .newBuilder()
                .addQueryParameter("label", properties.getPrompt().getEnvLabel())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Basic " + auth)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to fetch all prompt {}", response.code());
                return Collections.emptyList();
            }

            JsonNode rootNode = objectMapper.readTree(response.body().string());
            // Save them to storage
            JsonNode dataNode = rootNode.get("data");
            List<String> promptNames = new ArrayList<>();

            if (dataNode.isArray()) {
                for (JsonNode item : dataNode) {
                    if (item.has("name")) {
                        promptNames.add(item.get("name").asText());
                    }
                }
            }

            if (CollectionUtils.isEmpty(promptNames)) {
                return Collections.emptyList();
            }

            List<Prompt> prompts = new ArrayList<>();
            for (String promptName : promptNames) {
                Optional<Prompt> promptOpt = getPrompt(promptName);
                promptOpt.ifPresent(prompts::add);
            }
            return prompts;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse a prompt from JSON response
     *
     * @param jsonNode The JSON node containing prompt data
     * @return Optional containing the parsed Prompt if successful, empty otherwise
     */
    private Optional<Prompt> parsePromptFromJson(JsonNode jsonNode) {
        try {
            String type = jsonNode.path("type").asText("TEXT").toUpperCase();
            PromptBuilder promptBuilder = Prompt.builder()
                    .name(jsonNode.path("name").asText())
                    .version(jsonNode.path("version").asText())
                    .timestamp(System.currentTimeMillis())
                    .type(Prompt.Type.valueOf(type));

            // Handle labels
            List<String> labels = new ArrayList<>();
            jsonNode.path("labels").forEach(label -> labels.add(label.asText()));
            promptBuilder.labels(labels);

            // Handle tags
            List<String> tags = new ArrayList<>();
            jsonNode.path("tags").forEach(tag -> tags.add(tag.asText()));
            promptBuilder.tags(tags);

            // Handle commitMessage
            promptBuilder.commitMessage(jsonNode.path("commitMessage").asText());

            if (type.equals("CHAT")) {
                JsonNode messagesNode = jsonNode.path("prompt");
                if (!messagesNode.isArray()) {
                    throw new RuntimeException("chat messages is empty");
                }
                List<ChatMessage> messages = new ArrayList<>();
                for (JsonNode msgNode : messagesNode) {
                    messages.add(ChatMessage.builder()
                            .role(msgNode.path("role").asText())
                            .content(msgNode.path("content").asText())
                            .build());
                }
                promptBuilder.messages(messages);

            } else {
                // Default to TEXT type
                promptBuilder.prompt(jsonNode.path("prompt").asText());
            }

            promptBuilder.timestamp(System.currentTimeMillis());

            return Optional.of(promptBuilder.build());
        } catch (Exception e) {
            log.error("Error parsing prompt from JSON", e);
            return Optional.empty();
        }
    }

}
