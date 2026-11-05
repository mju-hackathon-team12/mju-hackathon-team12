package com.mju.dearseoul.domain.letter.dto;

import com.mju.dearseoul.domain.content.dto.ContentType;
import com.mju.dearseoul.domain.letter.entity.ExperienceLetter;
import java.time.Instant;
import java.util.List;

public record ExperienceLetterResponse(String id, String text, String placeId, List<Content> contents,
                                       String tag, boolean spoiler, boolean sample, boolean mine,
                                       String sourceLetterId, String routine, Instant createdAt) {
    public record Content(ContentType type, String title) {}

    public static ExperienceLetterResponse from(ExperienceLetter letter, String viewer) {
        return new ExperienceLetterResponse(letter.getId(), letter.getText(), letter.getPlaceId(),
                letter.getContents().stream().map(content -> new Content(content.getType(), content.getTitle())).toList(),
                letter.getTag(), letter.isSpoiler(), letter.isSample(), letter.getOwner().equals(viewer),
                letter.getSourceLetterId(), letter.getRoutine(), letter.getCreatedAt());
    }
}
