package com.mju.dearseoul.flow;

import com.mju.dearseoul.domain.recommendation.service.*;
import com.mju.dearseoul.domain.letter.service.LetterReviewService;
import com.mju.dearseoul.domain.letter.service.ExperienceLetterQueryService;
import com.mju.dearseoul.domain.letter.entity.ExperienceLetter;
import com.mju.dearseoul.domain.letter.repository.ExperienceLetterRepository;
import com.mju.dearseoul.domain.letter.repository.LetterReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:experience-workflow",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.ai.api-key="
})
@Import(ExperienceWorkflowTest.FakeAiConfiguration.class)
class ExperienceWorkflowTest {
    @LocalServerPort
    private int port;

    @Autowired
    private FakeAi ai;

    @Autowired
    private ExperienceLetterRepository letterRepository;

    @Autowired
    private ExperienceLetterQueryService letterQueries;

    @Autowired
    private LetterReviewRepository reviewRepository;

    private final JsonMapper json = JsonMapper.builder().build();

    @BeforeEach
    void resetAi() {
        ai.invalidIds.set(false);
        ai.calls.set(0);
        ai.lastCandidates.set(null);
        ai.reviewDecision.set(null);
    }

    private HttpClient browser() throws Exception {
        var browser = HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
        get(browser, "/api/bootstrap");
        return browser;
    }

    private HttpResponse<String> get(HttpClient browser, String path) throws Exception {
        return browser.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(HttpClient browser, String path, Object body) throws Exception {
        return browser.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body))).build(), HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode body(HttpResponse<String> response) {
        return json.readTree(response.body());
    }

    private Map<String, Object> preference(String district) {
        return Map.of("ratings", List.of(
                Map.of("title", "나의 아저씨", "cat", "drama", "score", 5),
                Map.of("title", "밤편지", "cat", "music", "score", 4),
                Map.of("title", "내가 직접 입력한 영화", "cat", "film", "score", 2)), "mood", "", "gu", district);
    }

    private String createPreference(HttpClient browser, String district) throws Exception {
        var response = post(browser, "/api/preferences", preference(district));
        assertEquals(200, response.statusCode(), response.body());
        return body(response).path("preferenceId").asString();
    }

    @Test
    void developmentH2ConsoleIsReachableAndOnlyItsFramesAreAllowed() throws Exception {
        var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        var console = get(client, "/h2-console/");
        assertEquals(200, console.statusCode(), console.body());
        // H2's landing page redirects with JavaScript rather than an HTTP Location header.
        var redirect = java.util.regex.Pattern.compile("location\\.href = '(login\\.jsp\\?jsessionid=[^']+)'").matcher(console.body());
        assertTrue(redirect.find(), console.body());
        var login = get(client, "/h2-console/" + redirect.group(1));
        assertEquals(200, login.statusCode(), login.body());
        assertTrue(login.body().contains("name=\"url\""), login.body());
        assertEquals("SAMEORIGIN", login.headers().firstValue("X-Frame-Options").orElseThrow());
        assertEquals("SAMEORIGIN", console.headers().firstValue("X-Frame-Options").orElseThrow());
        assertEquals("DENY", get(client, "/api/health").headers().firstValue("X-Frame-Options").orElseThrow());
        assertEquals("DENY", get(client, "/h2-console-other").headers().firstValue("X-Frame-Options").orElseThrow());
    }

    @Test
    void removedApisAreNotExposedAndCurrentRequestsStillRejectOtherOrigins() throws Exception {
        var browser = browser();
        for (String path : List.of("/api/places", "/api/songs", "/api/places/1/records", "/api/me/letters")) {
            assertEquals(404, get(browser, path).statusCode(), path);
        }
        for (String path : List.of("/api/recommendations/routine", "/api/records", "/api/records/old/remix", "/api/records/old/letters")) {
            assertEquals(404, post(browser, path, Map.of()).statusCode(), path);
        }
        var health = get(browser, "/api/health");
        assertEquals(200, health.statusCode());
        assertFalse(body(health).path("aiConfigured").asBoolean());
        var rejected = browser.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/preferences"))
                .header("Content-Type", "application/json").header("Origin", "https://other.example")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(preference("서울 전체")))).build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(403, rejected.statusCode());
        assertEquals("ORIGIN_REJECTED", body(rejected).path("code").asString());
    }

    @Test
    void seenLettersDoNotFillTheCandidateQueryLimit() {
        List<ExperienceLetter> seen = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            seen.add(new ExperienceLetter("seen-" + UUID.randomUUID(), "another-owner", null, null,
                    "falls", "이미 추천받은 레터", "comfort", false, false, null, "", List.of()));
        }
        letterRepository.saveAllAndFlush(seen);
        try {
            Set<String> excludedIds = new HashSet<>(seen.stream().map(ExperienceLetter::getId).toList());
            var candidates = letterQueries.candidates("visitor", null, excludedIds);
            assertTrue(candidates.stream().noneMatch(letter -> excludedIds.contains(letter.id())));
            assertTrue(candidates.stream().map(letter -> letter.placeId()).distinct().count() >= 3);
            assertTrue(candidates.stream().anyMatch(letter -> letter.id().equals("sample-01")));
        } finally {
            letterRepository.deleteAll(seen);
        }
    }

    @Test
    void moreThanOneHundredRecentLettersAtOneMailboxDoNotHideOtherMailboxes() {
        List<ExperienceLetter> popular = new ArrayList<>();
        for (int i = 0; i < 105; i++) {
            popular.add(new ExperienceLetter("popular-" + UUID.randomUUID(), "another-owner", null, null,
                    "falls", "새로 올라온 산책 이야기", "comfort", false, false, null, "", List.of()));
        }
        letterRepository.saveAllAndFlush(popular);
        try {
            var candidates = letterQueries.candidates("visitor", "서대문구", Set.of());
            assertTrue(candidates.stream().map(letter -> letter.placeId()).distinct().count() >= 3,
                    "Older letters at other mailboxes must remain eligible");
            assertEquals(10, candidates.stream().filter(letter -> letter.placeId().equals("falls")).count(),
                    "Bound candidates per mailbox instead of limiting the whole result to 100");
            assertTrue(candidates.stream().anyMatch(letter -> letter.id().equals("sample-02")));
            assertTrue(letterQueries.candidates("visitor", "강남구", Set.of()).isEmpty());
        } finally {
            letterRepository.deleteAll(popular);
        }
    }

    @Test
    void bootstrapReturnsSqlSeededCatalogsAndPersistedSampleLetters() throws Exception {
        var response = get(browser(), "/api/bootstrap");
        assertEquals(200, response.statusCode());
        assertEquals(30, body(response).path("contents").size());
        assertEquals(162, body(response).path("places").size());
        assertFalse(body(response).path("temporaryContents").asBoolean());
        assertTrue(body(response).path("letters").size() >= 10);
        for (var letter : body(response).path("letters")) {
            assertFalse(letter.has("owner"));
            assertFalse(letter.has("requestKey"));
        }
        var catalog = body(get(browser(), "/api/contents")).path("items");
        Map<String, Integer> counts = new HashMap<>();
        for (var content : catalog) counts.merge(content.path("type").asString(), 1, Integer::sum);
        assertEquals(Map.of("music", 10, "film", 10, "drama", 10), counts);
    }

    @Test
    void preferencesAreValidatedAndOwnedByTheirAnonymousBrowser() throws Exception {
        var a = browser();
        var b = browser();
        String preferenceId = createPreference(a, "서울 전체");
        assertEquals(404, post(b, "/api/recommendations", Map.of("preferenceId", preferenceId)).statusCode());
        assertEquals(400, post(a, "/api/preferences", Map.of("ratings", List.of())).statusCode());
        var invalidScore = Map.of("ratings", List.of(
                Map.of("title", "A", "cat", "music", "score", 0),
                Map.of("title", "B", "cat", "film", "score", 5),
                Map.of("title", "C", "cat", "drama", "score", 5)));
        assertEquals(400, post(a, "/api/preferences", invalidScore).statusCode());
        var invalidType = Map.of("ratings", List.of(
                Map.of("title", "A", "cat", "activity", "score", 5),
                Map.of("title", "B", "cat", "film", "score", 5),
                Map.of("title", "C", "cat", "drama", "score", 5)));
        assertEquals(400, post(a, "/api/preferences", invalidType).statusCode());
    }

    @Test
    void recommendationReturnsExactlyThreeRealLettersAtDifferentPlacesAndAvoidsPreviousBatch() throws Exception {
        var browser = browser();
        String preferenceId = createPreference(browser, "서대문구");
        var first = post(browser, "/api/recommendations", Map.of("preferenceId", preferenceId));
        assertEquals(200, first.statusCode(), first.body());
        var results = body(first).path("letters");
        assertEquals(3, results.size());
        Set<String> ids = new HashSet<>();
        Set<String> places = new HashSet<>();
        for (var result : results) {
            var letter = result.path("letter");
            assertTrue(ids.add(letter.path("id").asString()));
            assertTrue(places.add(letter.path("placeId").asString()));
            assertEquals(letter.path("text").asString(), body(get(browser, "/api/letters/" + letter.path("id").asString())).path("text").asString());
        }
        var next = post(browser, "/api/recommendations", Map.of("preferenceId", preferenceId));
        assertEquals(200, next.statusCode(), next.body());
        for (var item : body(next).path("letters")) assertFalse(ids.contains(item.path("letter").path("id").asString()));
    }

    @Test
    void emptyDistrictAndFabricatedAiIdsAreRejectedWithoutFakeFallback() throws Exception {
        var browser = browser();
        String preferenceId = createPreference(browser, "강남구");
        var response = post(browser, "/api/recommendations", Map.of("preferenceId", preferenceId));
        assertEquals(404, response.statusCode());
        assertEquals("NO_RECOMMENDABLE_LETTERS", body(response).path("code").asString());
        assertEquals(0, ai.calls.get());
        ai.invalidIds.set(true);
        preferenceId = createPreference(browser, "서울 전체");
        response = post(browser, "/api/recommendations", Map.of("preferenceId", preferenceId));
        assertEquals(502, response.statusCode());
        assertEquals("AI_INVALID_RESPONSE", body(response).path("code").asString());
    }

    @Test
    void directWritingIsStoredOnceAndDiscoveredByAnotherBrowserWithoutAnyPreference() throws Exception {
        var author = browser();
        var visitor = browser();
        String text = "나만의 이야기를 남기는 날 " + UUID.randomUUID();
        String work = "나만의 작품 " + UUID.randomUUID();
        var review = post(author, "/api/letters/review", Map.of("text", text, "work", work, "cat", "music", "placeId", "falls"));
        assertEquals(200, review.statusCode(), review.body());
        String reviewId = body(review).path("reviewId").asString();
        assertFalse(body(review).path("corrected").asBoolean());
        assertEquals(text, body(review).path("text").asString());
        var request = Map.of("requestId", UUID.randomUUID().toString(), "reviewId", reviewId, "placeId", "falls");
        assertEquals(404, post(visitor, "/api/letters", request).statusCode());
        assertEquals(400, post(author, "/api/letters", Map.of("requestId", UUID.randomUUID().toString(), "reviewId", reviewId, "placeId", "film")).statusCode());
        var saved = post(author, "/api/letters", request);
        assertEquals(200, saved.statusCode(), saved.body());
        String id = body(saved).path("id").asString();
        assertEquals(text, body(saved).path("text").asString());
        assertFalse(body(saved).path("sample").asBoolean());
        assertEquals(id, body(post(author, "/api/letters", request)).path("id").asString());
        assertEquals(id, body(post(author, "/api/letters", Map.of("requestId", UUID.randomUUID().toString(), "reviewId", reviewId, "placeId", "falls"))).path("id").asString());
        var publicLetter = body(get(visitor, "/api/letters/" + id));
        assertEquals(text, publicLetter.path("text").asString());
        assertFalse(publicLetter.path("mine").asBoolean());
        assertFalse(publicLetter.has("owner"));
        assertEquals("music", publicLetter.path("contents").path(0).path("type").asString());
        String preferenceId = createPreference(author, "서울 전체");
        var recommended = post(author, "/api/recommendations", Map.of("preferenceId", preferenceId));
        assertEquals(200, recommended.statusCode());
        for (var item : body(recommended).path("letters")) assertNotEquals(id, item.path("letter").path("id").asString());
        // Explicit affinity keeps this letter in the 30-item shortlist even as seed data grows.
        var visitorTaste = post(visitor, "/api/preferences", Map.of("ratings", List.of(
                Map.of("title", work, "cat", "music", "score", 5),
                Map.of("title", "중립 영화", "cat", "film", "score", 3),
                Map.of("title", "중립 드라마", "cat", "drama", "score", 3)), "gu", "서울 전체"));
        assertEquals(200, visitorTaste.statusCode(), visitorTaste.body());
        String visitorPreference = body(visitorTaste).path("preferenceId").asString();
        assertEquals(200, post(visitor, "/api/recommendations", Map.of("preferenceId", visitorPreference)).statusCode());
        assertTrue(ai.lastCandidates.get().candidates().stream().anyMatch(candidate -> candidate.id().equals(id)));
    }

    @Test
    void contactsReturnInputErrorsWithoutCallingAiOrCreatingReviews() throws Exception {
        var browser = browser();
        long before = reviewRepository.count();
        for (String contact : List.of("010-1234-5678", "+82 10 1234 5678", "02-123-4567", "hello@example.com")) {
            var response = post(browser, "/api/letters/review", Map.of("text", "산책 후 연락: " + contact));
            assertEquals(400, response.statusCode(), response.body());
            assertEquals("INVALID_INPUT", body(response).path("code").asString());
        }
        var workContact = post(browser, "/api/letters/review", Map.of("text", "산책이 좋았어요.", "work", "hello@example.com", "cat", "music"));
        assertEquals(400, workContact.statusCode());
        assertEquals(0, ai.calls.get());
        assertEquals(before, reviewRepository.count());
    }

    @Test
    void minorIssuesAreCorrectedAndOnlyTheReviewedTextIsPersisted() throws Exception {
        var browser = browser();
        String original = "씨발 힘든 하루였어요. 그래도 내일도 걷고 싶어요.";
        String approved = "정말 힘든 하루였어요. 그래도 내일도 걷고 싶어요.";
        ai.reviewDecision.set(new LetterReviewService.Decision(false, false, "거친 표현만 순화했어요.", "rest",
                List.of(new LetterReviewService.PlaceSuggestion("falls", "산책하기 좋아요."),
                        new LetterReviewService.PlaceSuggestion("ansan", "쉬어가기 좋아요.")),
                approved));
        var review = post(browser, "/api/letters/review", Map.of("text", original, "placeId", "falls"));
        assertEquals(200, review.statusCode(), review.body());
        assertFalse(body(review).path("block").asBoolean());
        assertTrue(body(review).path("corrected").asBoolean());
        assertEquals(approved, body(review).path("text").asString());
        String reviewId = body(review).path("reviewId").asString();
        assertEquals(approved, reviewRepository.findById(reviewId).orElseThrow().getText());
        var request = Map.of("requestId", UUID.randomUUID().toString(), "reviewId", reviewId, "placeId", "falls");
        var saved = post(browser, "/api/letters", request);
        assertEquals(200, saved.statusCode(), saved.body());
        assertEquals(approved, body(saved).path("text").asString());
        assertEquals(approved, letterRepository.findById(body(saved).path("id").asString()).orElseThrow().getText());
        assertEquals(body(saved).path("id").asString(), body(post(browser, "/api/letters", request)).path("id").asString());
    }

    @Test
    void fullLetterReplacementIsAlsoSavableAfterProfanityReview() throws Exception {
        var browser = browser();
        String original = "씨발";
        String approved = "정말 힘든 마음이에요.";
        ai.reviewDecision.set(new LetterReviewService.Decision(false, false, "욕설을 순화했어요.", "comfort",
                List.of(new LetterReviewService.PlaceSuggestion("falls", "쉬어가기 좋아요."),
                        new LetterReviewService.PlaceSuggestion("ansan", "걷기 좋아요.")),
                approved));
        var review = post(browser, "/api/letters/review", Map.of("text", original, "placeId", "falls"));
        assertEquals(200, review.statusCode(), review.body());
        assertTrue(body(review).path("corrected").asBoolean());
        assertEquals(approved, body(review).path("text").asString());
        var saved = post(browser, "/api/letters", Map.of("requestId", UUID.randomUUID().toString(),
                "reviewId", body(review).path("reviewId").asString(), "placeId", "falls"));
        assertEquals(200, saved.statusCode(), saved.body());
        assertEquals(approved, body(saved).path("text").asString());
    }

    @Test
    void blockedContentCannotCreateAReviewAndFollowedLettersKeepTheirSource() throws Exception {
        var browser = browser();
        long before = reviewRepository.count();
        ai.reviewDecision.set(new LetterReviewService.Decision(true, false, "위협하는 내용은 저장할 수 없어요.", "comfort", List.of(), null));
        var blocked = post(browser, "/api/letters/review", Map.of("text", "내일 찾아가서 너를 죽여 버리겠다."));
        assertEquals(200, blocked.statusCode(), blocked.body());
        assertTrue(body(blocked).path("block").asBoolean());
        assertTrue(body(blocked).path("reviewId").isNull());
        assertTrue(body(blocked).path("text").isNull());
        assertEquals(before, reviewRepository.count());
        assertEquals(400, post(browser, "/api/letters", Map.of("requestId", UUID.randomUUID().toString(), "placeId", "falls")).statusCode());
        ai.reviewDecision.set(null);
        var review = post(browser, "/api/letters/review", Map.of("text", "다른 사람의 산책을 따라 걸었어요.", "sourceLetterId", "sample-01", "placeId", "ansan"));
        assertEquals(200, review.statusCode(), review.body());
        var saved = post(browser, "/api/letters", Map.of("requestId", UUID.randomUUID().toString(),
                "reviewId", body(review).path("reviewId").asString(), "placeId", "ansan"));
        assertEquals(200, saved.statusCode());
        assertEquals("sample-01", body(saved).path("sourceLetterId").asString());
        assertEquals(0, body(saved).path("contents").size());
    }

    @TestConfiguration
    static class FakeAiConfiguration {
        @Bean
        @Primary
        FakeAi fakeWorkflowAi() {
            return new FakeAi();
        }
    }

    static class FakeAi implements AiJsonClient {
        final AtomicBoolean invalidIds = new AtomicBoolean();
        final AtomicInteger calls = new AtomicInteger();
        final AtomicReference<LetterRecommendationService.Input> lastCandidates = new AtomicReference<>();
        final AtomicReference<LetterReviewService.Decision> reviewDecision = new AtomicReference<>();

        @Override
        public <T> T generateJson(String operation, String instructions, Object input, Class<T> responseType, UnaryOperator<T> validator) {
            calls.incrementAndGet();
            Object result;
            if (input instanceof LetterRecommendationService.Input recommendation) {
                lastCandidates.set(recommendation);
                Set<String> places = new HashSet<>();
                var selected = recommendation.candidates().stream().filter(candidate -> places.add(candidate.place().id())).limit(3)
                        .map(candidate -> new LetterRecommendationService.Pick(invalidIds.get() ? "not-a-real-letter" : candidate.id(), "선호한 작품의 정서와 어울리는 이야기예요.")).toList();
                result = new LetterRecommendationService.Decision(selected);
            } else if (input instanceof LetterReviewService.Input review) {
                result = reviewDecision.get() != null ? reviewDecision.get() : new LetterReviewService.Decision(false, false, "레터를 확인했어요.", "comfort",
                        review.candidates().stream().limit(2).map(place -> new LetterReviewService.PlaceSuggestion(place.id(), "편안히 머무르기 좋은 곳이에요.")).toList(), review.text());
            } else {
                throw new IllegalArgumentException("Unexpected test AI operation");
            }
            return validator.apply(responseType.cast(result));
        }
    }
}
