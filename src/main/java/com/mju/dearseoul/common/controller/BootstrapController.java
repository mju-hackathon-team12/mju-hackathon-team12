package com.mju.dearseoul.common.controller;

import com.mju.dearseoul.domain.content.dto.ContentItem;
import com.mju.dearseoul.domain.content.service.ContentCatalog;
import com.mju.dearseoul.domain.letter.dto.ExperienceLetterResponse;
import com.mju.dearseoul.domain.letter.service.ExperienceLetterQueryService;
import com.mju.dearseoul.domain.place.dto.LetterPlace;
import com.mju.dearseoul.domain.place.service.LetterPlaceCatalog;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bootstrap")
public class BootstrapController {
    public record Response(boolean temporaryContents, List<ContentItem> contents,
                           List<LetterPlace> places, List<ExperienceLetterResponse> letters) {}
    private final ContentCatalog contents;
    private final LetterPlaceCatalog places;
    private final ExperienceLetterQueryService letters;

    public BootstrapController(ContentCatalog contents, LetterPlaceCatalog places, ExperienceLetterQueryService letters) {
        this.contents = contents;
        this.places = places;
        this.letters = letters;
    }

    @GetMapping
    public Response bootstrap(HttpServletRequest request) {
        return new Response(contents.temporary(), contents.all(), places.all(), letters.latest((String) request.getAttribute("anonymousOwner")));
    }
}
