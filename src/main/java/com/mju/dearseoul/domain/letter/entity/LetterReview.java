package com.mju.dearseoul.domain.letter.entity;

import com.mju.dearseoul.domain.content.dto.ContentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LetterReview {
    @Id
    private String id;

    private String owner;

    @Column(name = "reviewed_text", nullable = false, length = 2000)
    private String text;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(length = 120)
    private String contentTitle;

    private String tag;
    private boolean spoiler;
    private String sourceLetterId;
    private Instant expiresAt;

    @ElementCollection
    @CollectionTable(name = "review_place", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "place_id")
    private List<String> allowedPlaceIds = new ArrayList<>();

    public LetterReview(String owner, String text, ContentType contentType, String contentTitle, String tag,
                        boolean spoiler, String sourceLetterId, List<String> allowedPlaceIds) {
        this.id = UUID.randomUUID().toString();
        this.owner = owner;
        this.text = text;
        this.contentType = contentType;
        this.contentTitle = contentTitle;
        this.tag = tag;
        this.spoiler = spoiler;
        this.sourceLetterId = sourceLetterId;
        this.allowedPlaceIds = new ArrayList<>(allowedPlaceIds);
        this.expiresAt = Instant.now().plusSeconds(1800);
    }
}
