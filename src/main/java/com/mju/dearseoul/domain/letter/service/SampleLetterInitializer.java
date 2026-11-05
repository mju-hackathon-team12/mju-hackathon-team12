package com.mju.dearseoul.domain.letter.service;

import com.mju.dearseoul.domain.letter.dto.ExperienceLetterResponse.Content;
import com.mju.dearseoul.domain.letter.entity.*;
import com.mju.dearseoul.domain.letter.repository.ExperienceLetterRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.util.List;

@Component
public class SampleLetterInitializer implements ApplicationRunner {
    public record Sample(String id, String placeId, String text, String tag, String routine,
                         List<Content> contents, boolean spoiler) {}
    private final ExperienceLetterRepository letters;
    private final JsonMapper json;

    public SampleLetterInitializer(ExperienceLetterRepository letters, JsonMapper json) {
        this.letters = letters;
        this.json = json;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments arguments) throws IOException {
        try (var input = new ClassPathResource("fixtures/letters.json").getInputStream()) {
            for (Sample sample : json.readValue(input, Sample[].class)) {
                if (letters.existsById(sample.id())) continue;
                letters.save(new ExperienceLetter(sample.id(), "sample-data", null, null, sample.placeId(),
                        sample.text(), sample.tag(), sample.spoiler(), true, null, sample.routine(),
                        sample.contents().stream().map(content -> new LetterContent(content.type(), content.title())).toList()));
            }
        }
    }
}
