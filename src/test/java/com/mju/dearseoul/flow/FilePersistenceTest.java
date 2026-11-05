package com.mju.dearseoul.flow;

import com.mju.dearseoul.DearseoulApplication;
import com.mju.dearseoul.domain.content.dto.ContentType;
import com.mju.dearseoul.domain.content.repository.CatalogContentRepository;
import com.mju.dearseoul.domain.place.repository.CatalogPlaceRepository;
import com.mju.dearseoul.domain.letter.dto.SaveLetterRequest;
import com.mju.dearseoul.domain.letter.entity.LetterReview;
import com.mju.dearseoul.domain.letter.repository.LetterReviewRepository;
import com.mju.dearseoul.domain.letter.repository.ExperienceLetterRepository;
import com.mju.dearseoul.domain.letter.service.ExperienceLetterWriteService;
import com.mju.dearseoul.domain.letter.service.ExperienceLetterQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class FilePersistenceTest {
    private ConfigurableApplicationContext start(Path directory) {
        return new SpringApplicationBuilder(DearseoulApplication.class).web(WebApplicationType.NONE).run(
                "--spring.datasource.url=jdbc:h2:file:" + directory.resolve("mailboxes").toString().replace('\\', '/') + ";DB_CLOSE_ON_EXIT=FALSE",
                "--app.ai.api-key=",
                "--logging.level.root=WARN");
    }

    @Test
    void lettersSurviveACompleteApplicationRestart() throws Exception {
        Path directory = Path.of("build", "test-data", "persistence-" + UUID.randomUUID()).toAbsolutePath();
        Files.createDirectories(directory);
        String experienceId;
        try (var first = start(directory)) {
            assertEquals(30, first.getBean(CatalogContentRepository.class).count());
            assertEquals(161, first.getBean(CatalogPlaceRepository.class).countBySourceName("TOURAPI"));
            first.getBean(JdbcTemplate.class).update("update catalog_content set title = ? where id = ?", "운영자가 수정한 제목", "melon-603382333");
            var reviewed = first.getBean(LetterReviewRepository.class).save(new LetterReview("owner-a", "새 워크플로우의 레터", ContentType.MUSIC,
                    "밤편지", "comfort", false, "sample-01", List.of("falls")));
            experienceId = first.getBean(ExperienceLetterWriteService.class).save("owner-a",
                    new SaveLetterRequest(UUID.randomUUID().toString(), reviewed.getId(), "falls")).id();
        }
        try (var second = start(directory)) {
            assertEquals(30, second.getBean(CatalogContentRepository.class).count(), "SQL seeds must not duplicate contents on restart");
            assertEquals(162, second.getBean(CatalogPlaceRepository.class).count(), "SQL seeds must not duplicate places on restart");
            assertEquals("운영자가 수정한 제목", second.getBean(CatalogContentRepository.class).findById("melon-603382333").orElseThrow().getTitle(),
                    "SQL seeds must preserve modified rows on restart");
            var experience = second.getBean(ExperienceLetterQueryService.class).get(experienceId, "owner-b");
            assertEquals("새 워크플로우의 레터", experience.text());
            assertEquals("sample-01", experience.sourceLetterId());
            assertEquals(ContentType.MUSIC, experience.contents().get(0).type());
            assertFalse(experience.mine());
            assertEquals(11, second.getBean(ExperienceLetterRepository.class).count(), "Sample initialization must not duplicate rows on restart");
        }
    }
}
