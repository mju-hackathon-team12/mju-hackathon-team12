package com.mju.dearseoul.flow;

import com.mju.dearseoul.domain.content.dto.ContentType;
import com.mju.dearseoul.domain.content.repository.CatalogContentRepository;
import com.mju.dearseoul.domain.content.service.ContentCatalog;
import com.mju.dearseoul.domain.place.repository.CatalogPlaceRepository;
import com.mju.dearseoul.domain.place.service.LetterPlaceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

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
    private final JsonMapper json = JsonMapper.builder().build();

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
