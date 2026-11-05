package com.mju.dearseoul.domain.recommendation.service;

import com.mju.dearseoul.domain.content.dto.ContentType;
import com.mju.dearseoul.domain.letter.dto.ExperienceLetterResponse;
import com.mju.dearseoul.domain.letter.service.ExperienceLetterQueryService;
import com.mju.dearseoul.domain.place.dto.LetterPlace;
import com.mju.dearseoul.domain.place.service.LetterPlaceCatalog;
import com.mju.dearseoul.domain.preference.dto.PreferenceSnapshot;
import com.mju.dearseoul.domain.preference.service.PreferenceService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LetterRecommendationServiceTest {
    @Test
    void popularMailboxCannotCrowdOutOtherPlacesFromAiCandidates() {
        var preferences = mock(PreferenceService.class);
        var letters = mock(ExperienceLetterQueryService.class);
        var history = mock(RecommendationHistoryService.class);
        var places = mock(LetterPlaceCatalog.class);
        var preference = new PreferenceSnapshot("preference", List.of(
                new PreferenceSnapshot.Rating("밤편지", ContentType.MUSIC, 5, "comfort")), "", null);
        when(preferences.get("viewer", "preference")).thenReturn(preference);
        when(history.seen("preference")).thenReturn(Set.of());
        List<ExperienceLetterResponse> candidates = new ArrayList<>();
        for (int i = 0; i < 30; i++) candidates.add(letter("popular-" + i, "falls", "comfort"));
        candidates.add(letter("forest", "ansan", "rest"));
        candidates.add(letter("cinema", "film", "rest"));
        when(letters.candidates("viewer", null, Set.of())).thenReturn(candidates);
        when(places.all()).thenReturn(List.of("falls", "ansan", "film").stream()
                .map(id -> new LetterPlace(id, id, "서대문구", 37.5, 126.9, "", "", "")).toList());
        AiJsonClient ai = new AiJsonClient() {
            @Override
            public <T> T generateJson(String operation, String instructions, Object input, Class<T> type, UnaryOperator<T> validate) {
                var request = (LetterRecommendationService.Input) input;
                assertEquals(30, request.candidates().size());
                Set<String> placeIds = new HashSet<>();
                var picks = request.candidates().stream().filter(candidate -> placeIds.add(candidate.place().id()))
                        .map(candidate -> new LetterRecommendationService.Pick(candidate.id(), "마음에 어울리는 곳이에요.")).toList();
                assertEquals(3, picks.size());
                return validate.apply(type.cast(new LetterRecommendationService.Decision(picks)));
            }
        };
        var service = new LetterRecommendationService(preferences, letters, history, places, ai);
        var result = service.recommend("viewer", "preference");
        assertEquals(3, result.letters().size());
        assertEquals(3, result.letters().stream().map(item -> item.letter().placeId()).distinct().count());
        verify(history).record(eq("preference"), argThat(ids -> ids.size() == 3));
    }

    private ExperienceLetterResponse letter(String id, String placeId, String tag) {
        return new ExperienceLetterResponse(id, "산책하며 남긴 이야기", placeId, List.of(), tag,
                false, false, false, null, "", Instant.now());
    }
}
