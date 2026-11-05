package com.mju.dearseoul.domain.preference.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferenceSession {
    @Id
    private String id;

    @Column(nullable = false)
    private String owner;

    @Column(length = 1000)
    private String mood;

    private String district;

    @ElementCollection
    @CollectionTable(name = "preference_rating", joinColumns = @JoinColumn(name = "preference_id"))
    @OrderColumn(name = "rating_order")
    private List<RatedContent> ratings = new ArrayList<>();

    private Instant createdAt;

    public PreferenceSession(String owner, String mood, String district, List<RatedContent> ratings) {
        this.id = UUID.randomUUID().toString();
        this.owner = owner;
        this.mood = mood;
        this.district = district;
        this.ratings = new ArrayList<>(ratings);
        this.createdAt = Instant.now();
    }
}
