package com.mju.dearseoul.domain.letter.service;

import com.mju.dearseoul.common.exception.BusinessException;
import com.mju.dearseoul.common.exception.ErrorCode;
import com.mju.dearseoul.domain.letter.dto.ExperienceLetterResponse;
import com.mju.dearseoul.domain.letter.repository.ExperienceLetterRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ExperienceLetterQueryService {
    private static final int CANDIDATES_PER_PLACE = 10;
    private final ExperienceLetterRepository letters;

    public ExperienceLetterQueryService(ExperienceLetterRepository letters) {
        this.letters = letters;
    }

    public List<ExperienceLetterResponse> latest(String viewer) {
        return letters.findAllByOrderByCreatedAtDescIdAsc(PageRequest.of(0, 200)).stream()
                .map(letter -> ExperienceLetterResponse.from(letter, viewer)).toList();
    }

    public ExperienceLetterResponse get(String id, String viewer) {
        return ExperienceLetterResponse.from(letters.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "레터를 찾을 수 없습니다.")), viewer);
    }

    public List<ExperienceLetterResponse> candidates(String owner, String district, Set<String> excludedIds) {
        // Bind a nonempty list even when exclusion is disabled; SQL IN () is not portable.
        var candidates = letters.findRecommendationCandidates(owner, district, !excludedIds.isEmpty(),
                excludedIds.isEmpty() ? Set.of("") : excludedIds, CANDIDATES_PER_PLACE);
        return candidates.stream().map(letter -> ExperienceLetterResponse.from(letter, owner)).toList();
    }
}
