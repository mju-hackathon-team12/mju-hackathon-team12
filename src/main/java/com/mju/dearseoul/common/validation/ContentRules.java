package com.mju.dearseoul.common.validation;

import com.mju.dearseoul.common.exception.BusinessException;
import com.mju.dearseoul.common.exception.ErrorCode;

import java.util.List;
import java.util.regex.Pattern;

/** Small local safeguard, not a comprehensive language moderation model. */
public final class ContentRules {
    private ContentRules() {}
    private static final Pattern CONTACT = Pattern.compile(
            "(?i)(https?://|www\\.|[\\w.+-]+@[\\w.-]+\\.[a-z]{2,}|(?:\\+82[- .]?)?0?1[016789][- .]?\\d{3,4}[- .]?\\d{4}|\\d{6}[- ]?[1-4]\\d{6})");
    private static final List<String> BLOCKED = List.of("죽어", "죽여", "병신", "씨발");

    public static String text(String value, String label, int max) {
        if (value == null || value.isBlank() || value.length() > max) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, label + "은(는) 1~" + max + "자로 입력해주세요.");
        }
        String clean = value.strip();
        if (clean.codePoints().anyMatch(c -> Character.isISOControl(c) && c != '\n' && c != '\t')) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, label + "에 사용할 수 없는 문자가 있습니다.");
        }
        return clean;
    }

    public static String publicText(String value, String label, int max) {
        String clean = text(value, label, max);
        String compact = clean.replaceAll("\\s", "");
        if (CONTACT.matcher(clean).find() || BLOCKED.stream().anyMatch(compact::contains)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "연락처·개인정보·상처가 되는 표현을 지우고 다시 작성해주세요.");
        }
        return clean;
    }

}
