package com.mju.dearseoul.flow;

import com.mju.dearseoul.domain.content.dto.ContentType;
import com.mju.dearseoul.domain.content.repository.CatalogContentRepository;
import com.mju.dearseoul.domain.content.service.ContentCatalog;
import com.mju.dearseoul.domain.letter.dto.ExperienceLetterResponse.Content;
import com.mju.dearseoul.domain.letter.repository.ExperienceLetterRepository;
import com.mju.dearseoul.domain.place.repository.CatalogPlaceRepository;
import com.mju.dearseoul.domain.place.service.LetterPlaceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:catalog-seeds", "spring.jpa.hibernate.ddl-auto=create-drop", "app.ai.api-key="})
@Transactional
class CatalogSeedTest {
    @Autowired
    private CatalogContentRepository contents;
    @Autowired
    private CatalogPlaceRepository places;
    @Autowired
    private ContentCatalog contentCatalog;
    @Autowired
    private LetterPlaceCatalog placeCatalog;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private ExperienceLetterRepository letters;
    private final JsonMapper json = JsonMapper.builder().build();

    @Test
    void importsFiftyLettersFromSqlWithValidPlacesAndOrderedContent() {
        var imported = letters.findAll().stream().filter(letter -> letter.getId().startsWith("dummy-letter-")).toList();
        assertEquals(50, imported.size());
        assertEquals(60, letters.count(), "Keep the original ten examples alongside the new fifty");
        assertEquals(41L, jdbc.queryForObject("select count(*) from experience_letter_content where letter_id like 'dummy-letter-%' and content_type = 'MUSIC'", Long.class));
        assertEquals(9L, jdbc.queryForObject("select count(*) from experience_letter_content where letter_id like 'dummy-letter-%' and content_type = 'FILM'", Long.class));
        var tags = Set.of("comfort", "rest", "miss", "youth", "courage");
        for (var saved : imported) {
            assertTrue(places.existsById(saved.getPlaceId()), saved.getId());
            assertTrue(tags.contains(saved.getTag()), saved.getId());
            assertFalse(saved.getText().isBlank());
            assertFalse(saved.getRoutine().isBlank());
            assertEquals(1, saved.getContents().size());
            assertFalse(saved.isSpoiler());
            assertTrue(saved.isSample());
            assertEquals("sample-data", saved.getOwner());
            assertNotNull(saved.getCreatedAt());
        }
        var first = letters.findById("dummy-letter-007").orElseThrow();
        assertEquals("ansan", first.getPlaceId());
        assertEquals("아침에 알람 끄고 다시 잘까 하다가 그냥 나왔다. 안산자락길은 처음 가봤는데 생각보다 길이 편해서 다행이었다. 올라가면서 머릿속에 있던 할 일이 자꾸 떠올랐는데 내려올 때쯤엔 좀 잠잠해졌다. 이럴 거면 진작 나올걸.", first.getText());
        var film = letters.findById("dummy-letter-009").orElseThrow().getContents().get(0);
        assertEquals(ContentType.FILM, film.getType());
        assertEquals("리틀 포레스트", film.getTitle());
        assertEquals(java.util.List.of(
                new Content(ContentType.DRAMA, "나의 아저씨"),
                new Content(ContentType.FILM, "리틀 포레스트"),
                new Content(ContentType.MUSIC, "밤편지")),
                letters.findById("sample-01").orElseThrow().getContents().stream()
                        .map(content -> new Content(content.getType(), content.getTitle())).toList());
    }

    @Test
    void importsAllFourSnapshotsAndKeepsOriginalMetadata() {
        for (var type : ContentType.values()) assertEquals(10, contents.countByType(type));
        assertFalse(contentCatalog.temporary());
        assertEquals(161, places.countBySourceName("TOURAPI"));
        assertEquals(1, places.countBySourceName("LEGACY_SAMPLE"));
        var song = contents.findById("melon-603382333").orElseThrow();
        assertEquals("NEON", song.getTitle());
        assertEquals("A2O, A2O LITE", song.getArtist());
        assertEquals(64022, json.readTree(song.getSourceJson()).path("item").path("likeCnt").asInt());
        var movie = contents.findById("kobis-20267697").orElseThrow();
        assertEquals("명탐정 코난: 30호 살인 사건", movie.getTitle());
        assertEquals(25379, json.readTree(movie.getSourceJson()).path("item").path("daily_audience").asInt());
        var drama = contents.findFirstByTypeAndTitle(ContentType.DRAMA, "사랑이 온다").orElseThrow();
        assertEquals("KBS2", drama.getChannel());
        assertTrue(json.readTree(drama.getSourceJson()).path("item").path("official_table_verified").asBoolean());
        var place = places.findById("tourapi-2900328").orElseThrow();
        assertEquals("강수사", place.getName());
        assertEquals(37.5713634885, place.getLat(), 0.00000001);
        assertEquals(126.9342059353, place.getLon(), 0.00000001);
        assertTrue(json.readTree(place.getSourceJson()).path("item").has("detail"));
        assertEquals("3452906", places.findById("falls").orElseThrow().getSourceId());
        assertEquals(162, placeCatalog.all().size());
    }

    @Test
    void catalogReadsDatabaseValuesRatherThanOldFixtures() {
        jdbc.update("update catalog_content set title = ? where id = ?", "DB에서 바꾼 제목", "melon-603382333");
        jdbc.update("update catalog_place set name = ? where id = ?", "DB에서 바꾼 장소", "falls");
        assertTrue(contentCatalog.all().stream().anyMatch(item -> item.title().equals("DB에서 바꾼 제목")));
        assertEquals("DB에서 바꾼 장소", placeCatalog.get("falls").name());
    }
}
