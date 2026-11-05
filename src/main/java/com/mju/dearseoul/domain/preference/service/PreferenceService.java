package com.mju.dearseoul.domain.preference.service;

import com.mju.dearseoul.common.exception.*;
import com.mju.dearseoul.common.validation.ContentRules;
import com.mju.dearseoul.domain.content.service.ContentCatalog;
import com.mju.dearseoul.domain.preference.dto.*;
import com.mju.dearseoul.domain.preference.entity.*;
import com.mju.dearseoul.domain.preference.repository.PreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PreferenceService {
    private static final Set<String> DISTRICTS = Set.of("강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구");
    private final PreferenceRepository preferences;
    private final ContentCatalog catalog;

    public PreferenceService(PreferenceRepository preferences, ContentCatalog catalog) {
        this.preferences = preferences;
        this.catalog = catalog;
    }

    @Transactional
    public String create(String owner, PreferenceRequest request) {
        if (request == null || request.ratings() == null || request.ratings().size() < 3 || request.ratings().size() > 50) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "작품을 3~50개 선택하고 별점을 남겨주세요.");
        }
        Set<String> unique = new HashSet<>();
        List<RatedContent> ratings = new ArrayList<>();
        for (var rating : request.ratings()) {
            if (rating == null || rating.cat() == null || rating.score() == null || rating.score() < 1 || rating.score() > 5) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "음악·영화·드라마를 선택하고 1~5점으로 평가해주세요.");
            }
            String title = ContentRules.text(rating.title(), "작품 이름", 120);
            if (!unique.add(rating.cat() + ":" + title.toLowerCase(Locale.ROOT))) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "같은 작품을 중복 평가할 수 없습니다.");
            }
            ratings.add(new RatedContent(rating.cat(), title, rating.score(), catalog.tag(rating.cat(), title)));
        }
        String district = request.gu() == null || request.gu().isBlank() || request.gu().equals("서울 전체") ? null : request.gu();
        if (district != null && !DISTRICTS.contains(district)) throw new BusinessException(ErrorCode.INVALID_INPUT, "서울 지역구를 선택해주세요.");
        String mood = request.mood() == null || request.mood().isBlank() ? "" : ContentRules.text(request.mood(), "자유 입력", 1000);
        return preferences.save(new PreferenceSession(owner, mood, district, ratings)).getId();
    }

    public PreferenceSnapshot get(String owner, String id) {
        if (id == null || id.isBlank()) throw new BusinessException(ErrorCode.INVALID_INPUT, "취향 입력을 먼저 완료해주세요.");
        var preference = preferences.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "이 브라우저의 취향 정보를 찾을 수 없습니다."));
        return new PreferenceSnapshot(preference.getId(), preference.getRatings().stream()
                .map(rating -> new PreferenceSnapshot.Rating(rating.getTitle(), rating.getCat(), rating.getScore(), rating.getTag())).toList(),
                preference.getMood(), preference.getDistrict());
    }
}
