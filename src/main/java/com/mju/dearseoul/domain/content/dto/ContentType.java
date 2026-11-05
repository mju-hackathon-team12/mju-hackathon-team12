package com.mju.dearseoul.domain.content.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.mju.dearseoul.common.exception.BusinessException;
import com.mju.dearseoul.common.exception.ErrorCode;
import java.util.Locale;

public enum ContentType {
    MUSIC, FILM, DRAMA;

    @JsonValue
    public String value() {
        return name().toLowerCase(Locale.ROOT);
    }

    @JsonCreator
    public static ContentType from(String value) {
        if (value == null) throw new BusinessException(ErrorCode.INVALID_INPUT, "콘텐츠 종류를 선택해주세요.");
        try {
            return valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "음악·영화·드라마만 선택할 수 있습니다.");
        }
    }
}
