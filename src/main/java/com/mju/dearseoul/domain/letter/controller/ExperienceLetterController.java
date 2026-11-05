package com.mju.dearseoul.domain.letter.controller;

import com.mju.dearseoul.domain.letter.dto.*;
import com.mju.dearseoul.domain.letter.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/letters")
public class ExperienceLetterController {
    private final ExperienceLetterQueryService letters;
    private final LetterReviewService reviews;
    private final ExperienceLetterWriteService writer;

    public ExperienceLetterController(ExperienceLetterQueryService letters, LetterReviewService reviews, ExperienceLetterWriteService writer) {
        this.letters = letters;
        this.reviews = reviews;
        this.writer = writer;
    }

    @GetMapping
    public List<ExperienceLetterResponse> all(HttpServletRequest request) {
        return letters.latest(owner(request));
    }

    @GetMapping("/{id}")
    public ExperienceLetterResponse get(HttpServletRequest request, @PathVariable String id) {
        return letters.get(id, owner(request));
    }

    @PostMapping("/review")
    public LetterReviewService.Response review(HttpServletRequest request, @RequestBody LetterReviewRequest body) {
        return reviews.review(owner(request), body);
    }

    @PostMapping
    public ExperienceLetterResponse save(HttpServletRequest request, @RequestBody SaveLetterRequest body) {
        return writer.save(owner(request), body);
    }

    private String owner(HttpServletRequest request) {
        return (String) request.getAttribute("anonymousOwner");
    }
}
