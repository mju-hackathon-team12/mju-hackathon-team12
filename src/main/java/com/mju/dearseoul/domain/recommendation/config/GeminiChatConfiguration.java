package com.mju.dearseoul.domain.recommendation.config;

import com.google.genai.Client;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.HttpRetryOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.core.type.AnnotatedTypeMetadata;

/** Explicit wiring keeps the rest of the application usable without an API key. */
@Configuration(proxyBeanMethods = false)
@Conditional(GeminiChatConfiguration.ApiKeyPresent.class)
public class GeminiChatConfiguration {
    @Bean(destroyMethod = "close")
    public Client geminiClient(
            @Value("${app.ai.api-key}") String apiKey,
            @Value("${app.ai.base-url}") String baseUrl,
            @Value("${app.ai.timeout-seconds}") int timeoutSeconds) {
        return Client.builder()
                .apiKey(apiKey)
                .vertexAI(false)
                .httpOptions(HttpOptions.builder()
                        .baseUrl(baseUrl)
                        .apiVersion("v1beta")
                        .timeout(Math.multiplyExact(Math.max(1, timeoutSeconds), 1000))
                        .retryOptions(HttpRetryOptions.builder().attempts(1).build())
                        .build())
                .build();
    }

    @Bean
    public GoogleGenAiChatModel geminiChatModel(Client geminiClient, @Value("${app.ai.model}") String model) {
        if (!model.matches("[a-zA-Z0-9.-]+")) {
            throw new IllegalArgumentException("Invalid Gemini model");
        }
        return GoogleGenAiChatModel.builder()
                .genAiClient(geminiClient)
                .options(GoogleGenAiChatOptions.builder()
                        .model(model)
                        .temperature(0.7)
                        .maxOutputTokens(1600)
                        .responseMimeType("application/json")
                        .includeThoughts(false)
                        .build())
                .retryTemplate(new RetryTemplate(RetryPolicy.withMaxRetries(0)))
                .build();
    }

    @Bean
    public ChatClient geminiChatClient(GoogleGenAiChatModel geminiChatModel) {
        return ChatClient.create(geminiChatModel);
    }

    static class ApiKeyPresent implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return !context.getEnvironment().getProperty("app.ai.api-key", "").isBlank();
        }
    }
}
