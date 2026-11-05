package com.mju.dearseoul.domain.preference.entity;

import com.mju.dearseoul.domain.content.dto.ContentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RatedContent {
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType cat;

    @Column(nullable = false, length = 120)
    private String title;

    private int score;
    private String tag;

    public RatedContent(ContentType cat, String title, int score, String tag) {
        this.cat = cat;
        this.title = title;
        this.score = score;
        this.tag = tag;
    }
}
