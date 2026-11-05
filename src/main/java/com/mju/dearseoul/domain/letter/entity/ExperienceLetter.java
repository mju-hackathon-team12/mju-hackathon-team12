package com.mju.dearseoul.domain.letter.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "experience_letter", indexes = @Index(columnList = "placeId,createdAt"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExperienceLetter {
    @Id
    private String id;

    @Column(nullable = false)
    private String owner;

    @Column(unique = true)
    private String requestKey;

    @Column(unique = true)
    private String reviewId;

    @Column(nullable = false)
    private String placeId;

    @Column(name = "letter_text", nullable = false, length = 2000)
    private String text;

    private String tag;
    private boolean spoiler;
    private boolean sample;
    private String sourceLetterId;

    @Column(length = 200)
    private String routine;

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(name = "experience_letter_content", joinColumns = @JoinColumn(name = "letter_id"))
    @OrderColumn(name = "content_order")
    private List<LetterContent> contents = new ArrayList<>();

    private Instant createdAt;

    public ExperienceLetter(String id, String owner, String requestKey, String reviewId, String placeId,
                            String text, String tag, boolean spoiler, boolean sample, String sourceLetterId,
                            String routine, List<LetterContent> contents) {
        this.id = id;
        this.owner = owner;
        this.requestKey = requestKey;
        this.reviewId = reviewId;
        this.placeId = placeId;
        this.text = text;
        this.tag = tag;
        this.spoiler = spoiler;
        this.sample = sample;
        this.sourceLetterId = sourceLetterId;
        this.routine = routine;
        this.contents = new ArrayList<>(contents);
        this.createdAt = Instant.now();
    }
}
