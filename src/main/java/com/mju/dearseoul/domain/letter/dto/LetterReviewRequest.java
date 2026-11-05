package com.mju.dearseoul.domain.letter.dto;

import com.mju.dearseoul.domain.content.dto.ContentType;

public record LetterReviewRequest(String work, ContentType cat, String text, String placeId, String sourceLetterId) {}
