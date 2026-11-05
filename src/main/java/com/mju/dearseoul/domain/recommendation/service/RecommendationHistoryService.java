package com.mju.dearseoul.domain.recommendation.service;

import com.mju.dearseoul.domain.recommendation.entity.RecommendationHistory;
import com.mju.dearseoul.domain.recommendation.repository.RecommendationHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationHistoryService {
    private final RecommendationHistoryRepository history;

    public RecommendationHistoryService(RecommendationHistoryRepository history) {
        this.history = history;
    }

    @Transactional(readOnly = true)
    public Set<String> seen(String preferenceId) {
        return history.findByPreferenceId(preferenceId).stream().map(RecommendationHistory::getLetterId).collect(Collectors.toSet());
    }

    @Transactional
    public void record(String preferenceId, List<String> letterIds) {
        history.saveAll(letterIds.stream().map(id -> new RecommendationHistory(preferenceId, id)).toList());
    }
}
