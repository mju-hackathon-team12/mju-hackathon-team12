package com.mju.dearseoul.domain.preference.dto;

import com.mju.dearseoul.domain.content.dto.ContentType;
import java.util.List;

public record PreferenceSnapshot(String id, List<Rating> ratings, String mood, String district) {
    public record Rating(String title, ContentType cat, int score, String tag) {}
}
