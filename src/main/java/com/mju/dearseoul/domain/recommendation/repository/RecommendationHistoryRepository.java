package com.mju.dearseoul.domain.recommendation.repository;

import com.mju.dearseoul.domain.recommendation.entity.RecommendationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecommendationHistoryRepository extends JpaRepository<RecommendationHistory, String> {
    List<RecommendationHistory> findByPreferenceId(String preferenceId);
}
