package com.mju.dearseoul.domain.recommendation.controller;

import com.mju.dearseoul.common.exception.*;
import com.mju.dearseoul.domain.recommendation.service.LetterRecommendationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class LetterRecommendationController {
    public record Request(String preferenceId) {}
    private final LetterRecommendationService recommendations;

    public LetterRecommendationController(LetterRecommendationService recommendations) {
        this.recommendations = recommendations;
    }

    @PostMapping
    public LetterRecommendationService.Response recommend(HttpServletRequest request, @RequestBody Request body) {
        if (body == null) throw new BusinessException(ErrorCode.INVALID_INPUT);
        return recommendations.recommend((String) request.getAttribute("anonymousOwner"), body.preferenceId());
    }
}
