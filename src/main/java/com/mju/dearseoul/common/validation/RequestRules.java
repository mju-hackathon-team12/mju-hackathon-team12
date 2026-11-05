package com.mju.dearseoul.common.validation;

import com.mju.dearseoul.common.exception.BusinessException;
import com.mju.dearseoul.common.exception.ErrorCode;
import java.util.UUID;

public final class RequestRules {
    private RequestRules() {}

    public static String key(String owner, String requestId) {
        try {
            return owner + ":" + UUID.fromString(requestId);
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "requestId에는 새 UUID를 입력해주세요.");
        }
    }

}
