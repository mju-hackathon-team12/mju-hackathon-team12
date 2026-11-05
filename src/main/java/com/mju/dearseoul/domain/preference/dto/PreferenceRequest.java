package com.mju.dearseoul.domain.preference.dto;

import com.mju.dearseoul.domain.content.dto.ContentType;
import java.util.List;

public record PreferenceRequest(List<Rating> ratings, String mood, String gu) {
    public record Rating(String title, ContentType cat, Integer score) {}
}
