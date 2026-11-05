package com.mju.dearseoul.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "필수 입력과 형식을 확인해주세요."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 대상을 찾을 수 없습니다."),
    ORIGIN_REJECTED(HttpStatus.FORBIDDEN, "같은 사이트에서 요청해주세요."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 요청 방식입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 요청 데이터 형식입니다."),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "요청한 응답 형식을 제공할 수 없습니다."),
    DUPLICATE_REQUEST(HttpStatus.CONFLICT, "같은 요청을 처리 중입니다. 잠시 후 같은 requestId로 다시 확인해주세요."),
    AI_NOT_CONFIGURED(HttpStatus.SERVICE_UNAVAILABLE, "AI 추천 준비 중입니다. 서버에 GEMINI_API_KEY를 설정해주세요."),
    AI_BUSY(HttpStatus.TOO_MANY_REQUESTS, "다른 추천을 만들고 있습니다. 잠시 후 다시 시도해주세요."),
    NO_RECOMMENDABLE_LETTERS(HttpStatus.NOT_FOUND, "선택한 지역에 새로 추천할 우체통 3곳이 아직 없습니다. 서울 전체나 다른 취향으로 다시 찾아주세요."),
    REVIEW_EXPIRED(HttpStatus.CONFLICT, "레터 확인 시간이 지났습니다. 다시 AI 확인을 받아주세요."),
    AI_RATE_LIMIT(HttpStatus.TOO_MANY_REQUESTS, "추천 요청이 많습니다. 1분 뒤 다시 시도해주세요."),
    AI_QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "AI 무료 사용 한도에 도달했습니다. 잠시 후 다시 시도하거나 AI Studio에서 한도를 확인해주세요."),
    AI_AUTH_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "AI 키 또는 모델 접근 권한을 확인해주세요."),
    AI_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "추천 생성 시간이 길어졌습니다. 다시 시도해주세요."),
    AI_INTERRUPTED(HttpStatus.SERVICE_UNAVAILABLE, "추천이 중단되었습니다. 다시 시도해주세요."),
    AI_INVALID_RESPONSE(HttpStatus.BAD_GATEWAY, "추천 결과를 확인하지 못했습니다. 다시 시도해주세요."),
    AI_UPSTREAM_ERROR(HttpStatus.BAD_GATEWAY, "AI가 응답하지 못했습니다. 모델 설정을 확인하고 다시 시도해주세요."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return name();
    }
}
