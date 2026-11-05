package com.mju.dearseoul.domain.letter.service;

import com.mju.dearseoul.common.exception.BusinessException;
import com.mju.dearseoul.common.exception.ErrorCode;
import com.mju.dearseoul.domain.letter.dto.LetterReviewRequest;
import com.mju.dearseoul.domain.letter.entity.LetterReview;
import com.mju.dearseoul.domain.letter.repository.LetterReviewRepository;
import com.mju.dearseoul.domain.letter.service.LetterReviewService.Decision;
import com.mju.dearseoul.domain.letter.service.LetterReviewService.PlaceSuggestion;
import com.mju.dearseoul.domain.place.dto.LetterPlace;
import com.mju.dearseoul.domain.place.service.LetterPlaceCatalog;
import com.mju.dearseoul.domain.recommendation.service.AiJsonClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.List;
import java.util.function.UnaryOperator;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LetterReviewServiceTest {
    private final LetterReviewRepository reviews = mock(LetterReviewRepository.class);
    private final LetterPlaceCatalog places = mock(LetterPlaceCatalog.class);
    private Decision decision;
    private int aiCalls;
    private LetterReviewService service;

    @BeforeEach
    void setup() {
        var falls = new LetterPlace("falls", "폭포", "서대문구", 37.5, 126.9, "", "", "");
        var ansan = new LetterPlace("ansan", "안산", "서대문구", 37.5, 126.9, "", "", "");
        when(places.get("falls")).thenReturn(falls);
        when(places.all()).thenReturn(List.of(falls, ansan));
        when(places.contains("falls")).thenReturn(true);
        when(places.contains("ansan")).thenReturn(true);
        when(reviews.save(any(LetterReview.class))).thenAnswer(call -> call.getArgument(0));
        AiJsonClient ai = new AiJsonClient() {
            @Override
            public <T> T generateJson(String operation, String instructions, Object input,
                                      Class<T> responseType, UnaryOperator<T> validator) {
                aiCalls++;
                return validator.apply(responseType.cast(decision));
            }
        };
        service = new LetterReviewService(reviews, places, mock(ExperienceLetterQueryService.class), ai);
    }

    private Decision accept(String text) {
        return new Decision(false, false, "레터를 확인했어요.", "rest", List.of(
                new PlaceSuggestion("falls", "쉬어가기 좋아요."),
                new PlaceSuggestion("ansan", "산책하기 좋아요.")), text);
    }

    private LetterReviewService.Response review(String text) {
        return service.review("owner", new LetterReviewRequest("", null, text, "falls", null));
    }

    private void assertRejected(String text) {
        var error = assertThrows(BusinessException.class, () -> review(text));
        assertEquals(ErrorCode.AI_INVALID_RESPONSE, error.getErrorCode());
        verifyNoInteractions(reviews);
    }

    @Test
    void safeLettersKeepTheirOriginalWords() {
        String text = "20261106 오늘은 조용히 걸었어요.\n내일도 이 길을 걷고 싶어요.";
        decision = accept(text);
        var response = review(text);
        assertFalse(response.block());
        assertFalse(response.corrected());
        assertEquals(text, response.text());
    }

    @Test
    void correctedFullTextIsBoundToTheSavableReview() {
        String original = "씨발 힘든 하루였어요. 오늘의 나는 병신 같았어요. 그래도 산책은 좋았어요.";
        String approved = "정말 힘든 하루였어요. 오늘의 나는 서툴렀어요. 그래도 산책은 좋았어요.";
        decision = accept(approved);
        var response = review(original);
        assertTrue(response.corrected());
        assertEquals(approved, response.text());
        var saved = ArgumentCaptor.forClass(LetterReview.class);
        verify(reviews).save(saved.capture());
        assertEquals(approved, saved.getValue().getText());
        assertEquals(List.of("falls"), saved.getValue().getAllowedPlaceIds());
    }

    @Test
    void editingTheCorrectedLetterAndSubmittingAgainCreatesAFreshReview() {
        decision = accept("정말 힘든 하루였어요.");
        var first = review("씨발 힘든 하루였어요.");
        decision = accept("아주 답답한 하루였어요.");
        var second = review("씨발 답답한 하루였어요.");
        assertTrue(first.corrected());
        assertTrue(second.corrected());
        assertEquals("아주 답답한 하루였어요.", second.text());
        assertNotEquals(first.reviewId(), second.reviewId());
        assertEquals(2, aiCalls);
        verify(reviews, times(2)).save(any(LetterReview.class));
    }

    @Test
    void severeIssuesNeverCreateASavableReview() {
        decision = new Decision(true, null, "씨발 같은 원문을 인용한 잘못된 안내", null, null, null);
        var response = review("내일 너를 찾아가 죽여 버리겠다.");
        assertTrue(response.block());
        assertFalse(response.corrected());
        assertNull(response.reviewId());
        assertNull(response.text());
        assertTrue(response.places().isEmpty());
        assertFalse(response.note().contains("씨발"));
        verifyNoInteractions(reviews);
    }

    @Test
    void evenAOneWordLetterCanBeReplacedCompletely() {
        decision = accept("정말 힘든 마음이에요.");
        var response = review("씨발");
        assertTrue(response.corrected());
        assertEquals("정말 힘든 마음이에요.", response.text());
        assertNotNull(response.reviewId());
    }

    @Test
    void contactPatternsAreRejectedBeforeCallingAi() {
        for (String contact : List.of("010-1234-5678", "01012345678", "+82 10 1234 5678",
                "02-123-4567", "031-1234-5678", "070-1234-5678", "hello+letter@example.com")) {
            var error = assertThrows(BusinessException.class, () -> review("산책 후 연락 주세요: " + contact));
            assertEquals(ErrorCode.INVALID_INPUT, error.getErrorCode(), contact);
        }
        assertEquals(0, aiCalls);
        verifyNoInteractions(reviews);
    }

    @Test
    void directlyChosenPlacesDoNotRequireAiPlaceSuggestions() {
        decision = new Decision(false, false, "확인했어요.", "rest", List.of(), "정말 힘들었어요.");
        var response = review("씨발 힘들었어요.");
        assertEquals("falls", response.places().get(0).id());
        assertEquals(1, response.places().size());
    }

    @Test
    void invalidExplanationsDoNotRejectASafeCorrectedLetter() {
        decision = new Decision(false, false, "씨발을 수정했다고 원문을 인용한 안내", "rest", List.of(), "정말 힘들었어요.");
        var response = review("씨발 힘들었어요.");
        assertTrue(response.corrected());
        assertEquals("비속어·욕설을 순화했어요.", response.note());
    }

    @Test
    void aiPlacementRequiresAtLeastOneRealPlace() {
        decision = new Decision(false, false, "확인했어요.", "rest",
                List.of(new PlaceSuggestion("fabricated", "가짜"), new PlaceSuggestion("falls", "씨발을 인용한 안내")), "정말 힘들었어요.");
        var response = service.review("owner", new LetterReviewRequest("", null, "씨발 힘들었어요.", null, null));
        assertEquals(List.of("falls"), response.places().stream().map(PlaceSuggestion::id).toList());
        assertFalse(response.places().get(0).why().contains("씨발"));
    }

    @Test
    void unsafeEmptyMissingOrOverlongCorrectedTextIsNotSaved() {
        decision = accept("오늘은 씨발 힘들었어요.");
        assertRejected("오늘은 씨발 힘들었어요.");
        decision = accept("연락처: 010-1234-5678");
        assertRejected("오늘도 걸었어요.");
        decision = accept("");
        assertRejected("씨발");
        decision = accept(null);
        assertRejected("씨발");
        decision = accept("가".repeat(401));
        assertRejected("씨발 힘들었어요.");
    }
}
