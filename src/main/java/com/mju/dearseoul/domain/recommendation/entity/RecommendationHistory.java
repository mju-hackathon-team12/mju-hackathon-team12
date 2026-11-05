package com.mju.dearseoul.domain.recommendation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"preferenceId", "letterId"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationHistory {
    @Id
    private String id;

    private String preferenceId;
    private String letterId;
    private Instant createdAt;

    public RecommendationHistory(String preferenceId, String letterId) {
        this.id = UUID.randomUUID().toString();
        this.preferenceId = preferenceId;
        this.letterId = letterId;
        this.createdAt = Instant.now();
    }
}
