package com.mju.dearseoul.domain.letter.entity;

import com.mju.dearseoul.domain.content.dto.ContentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LetterContent {
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType type;

    @Column(name = "content_title", nullable = false, length = 120)
    private String title;

    public LetterContent(ContentType type, String title) {
        this.type = type;
        this.title = title;
    }
}
