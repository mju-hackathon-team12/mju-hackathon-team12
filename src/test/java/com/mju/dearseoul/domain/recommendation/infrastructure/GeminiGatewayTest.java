package com.mju.dearseoul.domain.recommendation.infrastructure;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.mju.dearseoul.common.exception.BusinessException;
import com.mju.dearseoul.common.exception.ErrorCode;
import com.mju.dearseoul.domain.recommendation.config.GeminiChatConfiguration;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class GeminiGatewayTest {
    private HttpServer server;
    private GeminiGateway gateway;
    private AnnotationConfigApplicationContext context;
    private final JsonMapper json = JsonMapper.builder().build();
    private int status;
    private long delay;
    private String response;
    private final AtomicReference<String> captured = new AtomicReference<>();
    private final AtomicInteger requestCount = new AtomicInteger();
    private Logger apiLogger;
    private Level originalLevel;
    private ListAppender<ILoggingEvent> logEvents;

    @BeforeEach
    void setup() throws Exception {
        apiLogger = (Logger) LoggerFactory.getLogger("com.mju.dearseoul.external.api");
        originalLevel = apiLogger.getLevel();
        apiLogger.setLevel(Level.DEBUG);
        logEvents = new ListAppender<>();
        logEvents.setContext(apiLogger.getLoggerContext());
        logEvents.start();
        apiLogger.addAppender(logEvents);
        status = 200;
        response = envelope("레터를 확인했어요.");
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1beta/models/test-model:generateContent", exchange -> {
            requestCount.incrementAndGet();
            captured.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            if (delay > 0) {
                try { Thread.sleep(delay); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        context = new AnnotationConfigApplicationContext();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context,
                "app.ai.api-key=test-key",
                "app.ai.base-url=http://127.0.0.1:" + server.getAddress().getPort(),
                "app.ai.model=test-model",
                "app.ai.timeout-seconds=3");
        context.register(GeminiChatConfiguration.class);
        context.refresh();
        gateway = new GeminiGateway(context.getBeanProvider(ChatClient.class));
    }

    @AfterEach
    void stop() {
        apiLogger.detachAppender(logEvents);
        apiLogger.setLevel(originalLevel);
        logEvents.stop();
        if (context != null) context.close();
        server.stop(0);
    }

    private record ReviewResult(String note) {}

    private String envelope(String note) {
        String output = json.writeValueAsString(Map.of("note", note));
        return json.writeValueAsString(Map.of("modelVersion", "test-model", "candidates", List.of(Map.of("finishReason", "STOP", "content", Map.of("role", "model", "parts", List.of(Map.of("text", output)))))));
    }

    private ReviewResult generate(GeminiGateway client) {
        return client.generateJson("letter-review", "레터를 검토하고 JSON으로 답하세요.",
                Map.of("text", "조용히 걸으며 쉬었어요.", "work", "밤편지"), ReviewResult.class, result -> {
                    if (result == null || result.note() == null || result.note().isBlank()) {
                        throw new BusinessException(ErrorCode.AI_INVALID_RESPONSE);
                    }
                    return result;
                });
    }

    @Test
    void decodesActualHttpProtocolWithoutLoggingPromptsOrCredentials() {
        assertEquals("레터를 확인했어요.", generate(gateway).note());
        var sent = json.readTree(captured.get());
        assertTrue(sent.has("systemInstruction") || sent.has("system_instruction"));
        var user = json.readTree(sent.path("contents").path(0).path("parts").path(0).path("text").asString());
        assertEquals("밤편지", user.path("work").asString());
        assertFalse(user.has("owner"));
        String logs = logs();
        assertTrue(logs.contains("external_api.start requestId="));
        assertTrue(logs.contains("external_api.success requestId="));
        assertTrue(logs.contains("operation=letter-review"));
        assertTrue(logs.contains("durationMs="));
        assertFalse(logs.contains("test-key"));
        assertFalse(logs.contains("밤편지"));
        assertFalse(logs.contains("조용히 걸으며"));
        assertFalse(logs.contains("레터를 확인했어요."));
    }

    @Test
    void rejectsMalformedAndInvalidAiOutput() {
        response = envelope("");
        assertEquals("AI_INVALID_RESPONSE", assertThrows(BusinessException.class, () -> generate(gateway)).getErrorCode().getCode());
        response = "{\"modelVersion\":\"test-model\",\"candidates\":[]}";
        assertEquals("AI_INVALID_RESPONSE", assertThrows(BusinessException.class, () -> generate(gateway)).getErrorCode().getCode());
    }

    @Test
    void preservesQuotaFailureAndRejectsMissingKey() {
        status = 429;
        response = "{\"error\":{\"code\":429,\"status\":\"RESOURCE_EXHAUSTED\",\"message\":\"quota reached\"}}";
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, assertThrows(BusinessException.class, () -> generate(gateway)).getErrorCode().getStatus());
        assertEquals(1, requestCount.get(), "SDK and Spring AI must not retry a quota failure");
        try (var missingKeyContext = new AnnotationConfigApplicationContext()) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(missingKeyContext, "app.ai.api-key=");
            missingKeyContext.register(GeminiChatConfiguration.class);
            missingKeyContext.refresh();
            var missingKey = new GeminiGateway(missingKeyContext.getBeanProvider(ChatClient.class));
            assertEquals(0, missingKeyContext.getBeanNamesForType(ChatClient.class).length);
            assertEquals("AI_NOT_CONFIGURED", assertThrows(BusinessException.class, () -> generate(missingKey)).getErrorCode().getCode());
        }
    }

    @Test
    void returnsATimeoutInsteadOfWaitingIndefinitely() {
        delay = 3400;
        assertEquals("AI_TIMEOUT", assertThrows(BusinessException.class, () -> generate(gateway)).getErrorCode().getCode());
        assertEquals(1, requestCount.get());
    }

    @Test
    void translatesAuthenticationAndProviderErrorsWithoutExposingTheirMessages() {
        status = 403;
        response = "{\"error\":{\"code\":403,\"status\":\"PERMISSION_DENIED\",\"message\":\"private upstream detail\"}}";
        BusinessException forbidden = assertThrows(BusinessException.class, () -> generate(gateway));
        assertEquals("AI_AUTH_FAILED", forbidden.getErrorCode().getCode());
        assertFalse(forbidden.getMessage().contains("private upstream detail"));
        status = 500;
        response = "{\"error\":{\"code\":500,\"status\":\"INTERNAL\",\"message\":\"private upstream detail\"}}";
        assertEquals("AI_UPSTREAM_ERROR", assertThrows(BusinessException.class, () -> generate(gateway)).getErrorCode().getCode());
        assertEquals(2, requestCount.get());
        String logs = logs();
        assertTrue(logs.contains("code=AI_AUTH_FAILED status=503 upstreamStatus=403"));
        assertTrue(logs.contains("code=AI_UPSTREAM_ERROR status=502 upstreamStatus=500"));
        assertFalse(logs.contains("private upstream detail"));
        assertFalse(logs.contains("test-key"));
    }

    private String logs() {
        return logEvents.list.stream().map(ILoggingEvent::getFormattedMessage)
                .collect(java.util.stream.Collectors.joining("\n"));
    }
}
