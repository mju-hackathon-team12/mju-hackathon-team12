package com.mju.dearseoul.domain.letter.service;

import com.mju.dearseoul.common.exception.BusinessException;
import com.mju.dearseoul.common.exception.ErrorCode;
import com.mju.dearseoul.domain.letter.dto.ExperienceLetterResponse;
import com.mju.dearseoul.domain.letter.repository.ExperienceLetterRepository;
import com.mju.dearseoul.domain.place.service.LetterPlaceCatalog;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ExperienceLetterQueryService {
    private final ExperienceLetterRepository letters;
    private final LetterPlaceCatalog places;

    public ExperienceLetterQueryService(ExperienceLetterRepository letters, LetterPlaceCatalog places) {
        this.letters = letters;
        this.places = places;
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
        List<String> ids = places.all().stream().filter(place -> district == null || place.district().equals(district))
                .map(place -> place.id()).toList();
        if (ids.isEmpty()) return List.of();
        var page = PageRequest.of(0, 100);
        // Exclude previously recommended letters before applying the result limit.
        var candidates = excludedIds.isEmpty()
                ? letters.findByPlaceIdInAndOwnerNotOrderByCreatedAtDescIdAsc(ids, owner, page)
                : letters.findByPlaceIdInAndOwnerNotAndIdNotInOrderByCreatedAtDescIdAsc(ids, owner, excludedIds, page);
        return candidates.stream().map(letter -> ExperienceLetterResponse.from(letter, owner)).toList();
    }
}
