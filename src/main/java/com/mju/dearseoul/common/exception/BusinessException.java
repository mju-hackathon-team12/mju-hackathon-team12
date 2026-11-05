package com.mju.dearseoul.common.exception;

import lombok.Getter;
import java.util.Objects;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    /** Custom messages must be safe to show to the user, never raw upstream exception text. */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode);
    }
}
