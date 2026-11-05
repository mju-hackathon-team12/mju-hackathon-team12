package com.mju.dearseoul.domain.letter.service;

import com.mju.dearseoul.common.exception.*;
import com.mju.dearseoul.common.validation.*;
import com.mju.dearseoul.domain.letter.dto.*;
import com.mju.dearseoul.domain.letter.entity.*;
import com.mju.dearseoul.domain.letter.repository.*;
import com.mju.dearseoul.domain.place.service.LetterPlaceCatalog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

@Service
public class ExperienceLetterWriteService {
    private final ExperienceLetterRepository letters;
    private final LetterReviewRepository reviews;
    private final LetterPlaceCatalog places;

    public ExperienceLetterWriteService(ExperienceLetterRepository letters, LetterReviewRepository reviews, LetterPlaceCatalog places) {
        this.letters = letters;
        this.reviews = reviews;
        this.places = places;
    }

    @Transactional
    public ExperienceLetterResponse save(String owner, SaveLetterRequest request) {
        if (request == null) throw new BusinessException(ErrorCode.INVALID_INPUT);
        String key = RequestRules.key(owner, request.requestId());
        var previous = letters.findByRequestKey(key);
        if (previous.isPresent()) return ExperienceLetterResponse.from(previous.get(), owner);
        if (request.reviewId() == null) throw new BusinessException(ErrorCode.INVALID_INPUT, "먼저 AI 확인을 받아주세요.");
        var review = reviews.findByIdAndOwner(request.reviewId(), owner)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "이 브라우저의 검토 결과를 찾을 수 없습니다."));
        var alreadySaved = letters.findByReviewId(review.getId());
        if (alreadySaved.isPresent()) return ExperienceLetterResponse.from(alreadySaved.get(), owner);
        if (!review.getExpiresAt().isAfter(Instant.now())) throw new BusinessException(ErrorCode.REVIEW_EXPIRED);
        places.get(request.placeId());
        if (!review.getAllowedPlaceIds().contains(request.placeId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "확인받은 우체통 중에서 선택해주세요.");
        }
        List<LetterContent> contents = review.getContentType() == null ? List.of()
                : List.of(new LetterContent(review.getContentType(), review.getContentTitle()));
        var letter = new ExperienceLetter(UUID.randomUUID().toString(), owner, key, review.getId(), request.placeId(),
                ContentRules.publicText(review.getText(), "레터", 400), review.getTag(), review.isSpoiler(), false,
                review.getSourceLetterId(), "", contents);
        return ExperienceLetterResponse.from(letters.saveAndFlush(letter), owner);
    }
}
