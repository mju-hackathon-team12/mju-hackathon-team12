package com.mju.dearseoul.domain.recommendation.infrastructure;

import com.mju.dearseoul.common.exception.BusinessException;
import com.mju.dearseoul.common.exception.ErrorCode;
import com.mju.dearseoul.domain.recommendation.service.AiJsonClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.InterruptedIOException;
import java.net.http.HttpTimeoutException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

@Slf4j(topic = "com.mju.dearseoul.external.api")
@Component
public class GeminiGateway implements AiJsonClient {
    private final ChatClient chatClient;
    private final JsonMapper json = JsonMapper.builder().build();
    private final Semaphore concurrent = new Semaphore(2);
    private final Deque<Long> requests = new ArrayDeque<>();

    public GeminiGateway(ObjectProvider<ChatClient> chatClients) {
        this.chatClient = chatClients.getIfAvailable();
    }

    private synchronized void limit() {
        long now = System.currentTimeMillis();
        while (!requests.isEmpty() && requests.peekFirst() < now - 60_000) requests.removeFirst();
        if (requests.size() >= 10) throw new BusinessException(ErrorCode.AI_RATE_LIMIT);
        requests.addLast(now);
    }

    @Override
    public <T> T generateJson(String operation, String instructions, Object input, Class<T> responseType,
                              UnaryOperator<T> validator) {
        String requestId = UUID.randomUUID().toString();
        long started = System.nanoTime();
        if (chatClient == null) {
            log.warn("external_api.skipped requestId={} provider=gemini operation={} code=AI_NOT_CONFIGURED", requestId, operation);
            throw new BusinessException(ErrorCode.AI_NOT_CONFIGURED);
        }
        if (!concurrent.tryAcquire()) {
            log.warn("external_api.skipped requestId={} provider=gemini operation={} code=AI_BUSY", requestId, operation);
            throw new BusinessException(ErrorCode.AI_BUSY);
        }
        RuntimeException transportFailure = null;
        try {
            limit();
            String userData = json.writeValueAsString(input);
            String content;
            log.debug("external_api.start requestId={} provider=gemini operation={}", requestId, operation);
            try {
                content = chatClient.prompt()
                        .system(instructions)
                        .user(userData)
                        .call()
                        .content();
            } catch (RuntimeException e) {
                transportFailure = e;
                throw translate(e);
            }
            log.debug("external_api.response requestId={} provider=gemini operation={} durationMs={} responseChars={}",
                    requestId, operation, elapsedMillis(started), content == null ? 0 : content.length());
            T result = validator.apply(json.readValue(content, responseType));
            log.debug("external_api.success requestId={} provider=gemini operation={} durationMs={}",
                    requestId, operation, elapsedMillis(started));
            return result;
        } catch (BusinessException e) {
            BusinessException failure = e;
            if (e.getErrorCode() == ErrorCode.INVALID_INPUT) {
                failure = new BusinessException(ErrorCode.AI_INVALID_RESPONSE);
            }
            logFailure(requestId, operation, started, failure, transportFailure == null ? e : transportFailure);
            throw failure;
        } catch (Exception e) {
            BusinessException failure = new BusinessException(ErrorCode.AI_INVALID_RESPONSE);
            logFailure(requestId, operation, started, failure, e);
            throw failure;
        } finally { concurrent.release(); }
    }

    private void logFailure(String requestId, String operation, long started, BusinessException failure, Exception cause) {
        // Log only allowlisted metadata. SDK messages/stack traces may contain prompts or credentials.
        String causeType = cause.getClass().getSimpleName();
        int upstreamStatus = 0;
        for (Throwable current = cause; current != null; current = current.getCause()) {
            causeType = current.getClass().getSimpleName();
            if (current instanceof com.google.genai.errors.ApiException upstream) upstreamStatus = upstream.code();
        }
        var event = failure.getErrorCode().getStatus().is5xxServerError() ? log.atError() : log.atWarn();
        event.log("external_api.failure requestId={} provider=gemini operation={} durationMs={} code={} status={} upstreamStatus={} causeType={}",
                requestId, operation, elapsedMillis(started), failure.getErrorCode().getCode(), failure.getErrorCode().getStatus().value(), upstreamStatus, causeType);
    }

    private long elapsedMillis(long started) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);
    }

    private BusinessException translate(RuntimeException failure) {
        for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
            if (cause instanceof NoSuchElementException) {
                return new BusinessException(ErrorCode.AI_INVALID_RESPONSE);
            }
            if (cause instanceof HttpTimeoutException || cause instanceof InterruptedIOException) {
                return new BusinessException(ErrorCode.AI_TIMEOUT);
            }
            if (cause instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                return new BusinessException(ErrorCode.AI_INTERRUPTED);
            }
            if (cause instanceof com.google.genai.errors.ApiException upstream) {
                int status = upstream.code();
                if (status == 429) {
                    return new BusinessException(ErrorCode.AI_QUOTA_EXCEEDED);
                }
                if (status == 401 || status == 403) {
                    return new BusinessException(ErrorCode.AI_AUTH_FAILED);
                }
                if (status == 408 || status == 504) {
                    return new BusinessException(ErrorCode.AI_TIMEOUT);
                }
                return new BusinessException(ErrorCode.AI_UPSTREAM_ERROR);
            }
        }
        return new BusinessException(ErrorCode.AI_UPSTREAM_ERROR);
    }

}
